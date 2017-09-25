$java = 'C:\Program Files\Java\jdk1.7.0_79\bin\java'
$cp = '.\out\production\purano\;lib\*'
$purano = 'jp.ac.osakau.farseerfc.purano.reflect.ClassFinder'
$pkg = 'jolden.bh'

& $java -cp $cp $purano $pkg