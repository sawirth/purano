package jp.ac.osakau.farseerfc.purano.reflect;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import ch.sawirth.serialization.JsonSerializer;
import com.martiansoftware.jsap.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.google.common.base.Joiner;

@Slf4j
public class ClassFinder {
	public Path findSourcePath(String name) {
		for(String srcPrefix : sourcePrefix){
			String className = name.replace(".", "/") + ".java";
			Path path = Paths.get(srcPrefix, className);
			File file = path.toFile();
			if(file.exists() && file.isFile()){
				return path;
			}
		}

		return null;
	}



	@Getter final Map<String, ClassRep> classMap= new HashMap<>();

	final Set<String> classTargets = new HashSet<>() ;
    final List<String> prefix;

    private static final int MAX_LOAD_PASS = 3;

    private final boolean examChangedSignatures = true;
    private final boolean breakForloop = true;
	private @NotNull final List<String> sourcePrefix;

	public ClassFinder(@NotNull List<String> prefix, @NotNull List<String> sourcePrefix){
		this.sourcePrefix = sourcePrefix;
		findTargetClasses(prefix);
        this.prefix=prefix;
	}

	public ClassFinder(@NotNull List<String> prefix){
        this(prefix, new ArrayList<String>());
	}

	public ClassFinder(String string) {
		this(Arrays.asList(string));
	}

	public void resolveMethods() {
		int timestamp = 0;
		Set<ClassRep> allCreps = new HashSet<>(classMap.values());
		boolean changed;
		int pass = 0;
        int changedMethod = 0;

        List<Integer> changedMethodsTrace = new ArrayList<>();
        List<Integer> loadedClassesTrace = new ArrayList<>();
		do {
			changed = false;
			if(pass < MAX_LOAD_PASS){
			    allCreps = new HashSet<>(classMap.values());
                loadedClassesTrace.add(allCreps.size());
			}
            changedMethod = 0;

            Set<MethodRep> changedSignatures = new HashSet<>();
			for (ClassRep crep : allCreps ) {
				for (MethodRep mrep : crep.getAllMethods()) {
					if (mrep.isNeedResolve(this)) {
						if (mrep.resolve(++timestamp, this)) {
							changed = true;
                            changedMethod ++;
                            if(examChangedSignatures){
                            	changedSignatures.add(mrep);
                            }
						}
					}
				}
			}
            changedMethodsTrace.add(changedMethod);
            log.info(String.format("Pass: %d Classes: %s Changed Method: %d [%s]",
                    pass++,allCreps.size(),changedMethod,
                    Joiner.on(", ").join(changedMethodsTrace)));
            if(examChangedSignatures){
	            final int maxdump=2;
	            if(changedMethod>maxdump){
	                MethodRep [] top = new MethodRep [maxdump];
	                int i=0;
	                for(MethodRep mid : changedSignatures){
	                    if(i>=maxdump)break;
		                    top[i++]=mid;
	                }
//	            	log.info(Joiner.on(", ").join(top));
	            }else{
//	                for(MethodRep m:changedSignatures){
//	                	log.info(Joiner.on("\n").join(m.dump(this, new Types(), Escaper.getDummy())));
//	                }
	                if(breakForloop){
	                	break;
	                }
		        }
            }
		} while (changed);

        log.info("Loaded Classes: " + Joiner.on(", ").join(loadedClassesTrace));
        log.info("Changed methods: "+Joiner.on(", ").join(changedMethodsTrace));
	}

	private void findTargetClasses(@NotNull Collection<String> prefixes){

		for(String prefix:prefixes){
			Reflections reflections = new Reflections( prefix ,new SubTypesScanner(false));
	        classTargets.addAll( reflections.getStore().getSubTypesOf(Object.class.getName()));
	        classTargets.add(prefix);
		}
		for(String cls : classTargets){
			loadClass(cls);
		}
	}

	public ClassRep loadClass(@NotNull String classname){
		if(!classMap.containsKey(classname)){
			log.info("Loading {}", classname);
			if(classname.startsWith("[")){
				classMap.put(classname, new ClassRep(ArrayStub.class.getName(),this));
			}else{
				classMap.put(classname, new ClassRep(classname, this));
			}
		}
		return classMap.get(classname);
	}

	private void dumpForResult() {
		log.info("<<<<<<<<<<<<<<<<< Refactoring Candidates <<<<<<<<<<<<<<<<<<<");
		int count = 0;
		for (String clsName : classMap.keySet()) {
			boolean isTarget = classTargets.contains(clsName);
			for (String p : prefix) {
				if (clsName.startsWith(p)) {
					isTarget = true;
				}
			}

			if (!isTarget) {
				continue;
			}

			ClassRep classRep = classMap.get(clsName);

			for (MethodRep methodRep : classRep.getAllMethods()) {
				ASTForVisitor forv = new ASTForVisitor(methodRep);
				if (methodRep.getSourceNode() != null) {
					methodRep.getSourceNode().accept(forv);
				}

				for (RefactoringCandidate can : methodRep.getCandidates()) {
					log.info(String.format(
							"\"%s.%s\" has pure for-loop at line (%d-%d)",
							methodRep.getClassRep().getBaseName(),
							MethodRep.getId(methodRep.getInsnNode()),
							methodRep.getUnit().getLineNumber(
									can.getNode().getStartPosition()),
							methodRep.getUnit().getLineNumber(
									can.getNode().getStartPosition()
											+ can.getNode().getLength())));
					log.info(can.getNode().toString());

					count ++;

				}
			}
		}
		log.info(String.format("Found %d refactoring candidates", count));
	}

	public static void main(@NotNull String [] args) throws IOException, JSAPException {
		long start=System.currentTimeMillis();

		JSAP jsap = new JSAP();

		Parameter packageNameParameter = new FlaggedOption("package")
				.setStringParser(JSAP.STRING_PARSER)
				.setRequired(true)
				.setShortFlag('p')
				.setLongFlag("pkg")
				.setList(true)
				.setListSeparator(';')
				.setHelp("Name of the root package which should be analyzed. Multiple packages can be provided by separating with ';'");

		Parameter outputPathParameter = new FlaggedOption("output")
				.setStringParser(JSAP.STRING_PARSER)
				.setRequired(true)
				.setShortFlag('o')
				.setLongFlag("output")
				.setHelp("Output file path");

		Parameter showExtendedParameter = new Switch("extended")
				.setShortFlag('e')
				.setLongFlag("extended")
				.setHelp("If set non-user classes will be included in the result. Otherwise only classes from the provided package are included.");

		Parameter helpParameter = new Switch("help")
				.setShortFlag('h')
				.setLongFlag("help")
				.setHelp("Show help and usage.");

		Parameter templatePathParameter = new FlaggedOption("template")
				.setStringParser(JSAP.STRING_PARSER)
				.setDefault("templates")
				.setRequired(false)
				.setShortFlag('t')
				.setLongFlag("template")
				.setHelp("Path to the template folder for the HTML-Dumper");

		jsap.registerParameter(outputPathParameter);
		jsap.registerParameter(helpParameter);
		jsap.registerParameter(showExtendedParameter);
		jsap.registerParameter(packageNameParameter);
		jsap.registerParameter(templatePathParameter);

		JSAPResult config = jsap.parse(args);

		if (config.success()) {
			String[] packagesToAnalyze = config.getStringArray("package");
			String outputPath = config.getString("output");
			String templatePath = config.getString("template");
			boolean includeNonUserClasses = config.getBoolean("extended");
			analysis(packagesToAnalyze, outputPath, includeNonUserClasses, templatePath);
		} else if (config.getBoolean("help")) {
			printParameterHelp(jsap);
		} else {
			Iterator iter = config.getErrorMessageIterator();
			while(iter.hasNext()) {
				System.err.println("Error" + iter.next());
			}

			System.err.println();
			printParameterHelp(jsap);
		}

		System.out.println("Runtime: " + (System.currentTimeMillis() - start) + "ms");
	}

	private static void printParameterHelp(JSAP jsap) {
		System.err.println("Usage: java " + ClassFinder.class.getName());
		System.err.println(jsap.getUsage());
		System.err.println();
		System.err.println(jsap.getHelp());
	}

	private static void analysis(
			String[] packagesToAnalyze,
			String outputPath,
			boolean includeNonUserTargets,
			String templatePathName) throws IOException
	{
		ClassFinder cf = new ClassFinder(Arrays.asList(packagesToAnalyze));
		cf.resolveMethods();
		cf.dumpForResult();

		//HTML which contains the standard Purano output
		try {
			File htmlOutput = new File(outputPath + "/Purano-result.html");
			PrintStream ps = new PrintStream(new FileOutputStream(htmlOutput));
			ClassFinderDumpper dumpper = new HtmlDumpper(ps,cf, includeNonUserTargets, templatePathName);
			dumpper.dump();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		//JSON that contains a modified data structure for easier usage to document .java-Files
		Set<ClassRep> classesToSerialize = new HashSet<>(cf.classMap.values());
		if (!includeNonUserTargets) {
			classesToSerialize.clear();

			for (ClassRep classRep : cf.classMap.values()) {
				for (String pkg : packagesToAnalyze) {
					if (classRep.getName().contains(pkg)) {
						classesToSerialize.add(classRep);
					}
				}
			}
		}

		JsonSerializer jsonSerializer = new JsonSerializer(classesToSerialize, outputPath);
		jsonSerializer.serializeToGson();
	}
}
