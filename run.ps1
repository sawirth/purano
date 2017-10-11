$java = 'C:\Program Files\Java\jdk1.7.0_79\bin\java'
$puranoCP = 'C:\Users\Sandro\Documents\GitHub\purano\bin;C:\Users\Sandro\Documents\GitHub\purano\lib\*;'
$purano = 'jp.ac.osakau.farseerfc.purano.reflect.ClassFinder'

# Specify the root path to the .class files or the folder of the .jar file which should be analyzed
# If you want to analyze a jar file, add the path to the file with a '\*' at the end
$homeDir = 'C:\Users\Sandro\Downloads\Libraries\joda-time\*'

# Specify the package which should be analyzed
$pkg = 'org.joda'

# Specify the directory where the results should be saved
$outputPath = 'C:\Users\Sandro\Documents\GitHub\purano'

# If set to true, the result will contain all analyzed classes and not only the ones from the specified package
$showExtended = False

$cp = $puranoCP + $homeDir

if($showExtended) 
{
    & $java -cp $cp $purano --pkg $pkg -o $outputPath --extended
}
else 
{
    & $java -cp $cp $purano --pkg $pkg -o $outputPath 
}
