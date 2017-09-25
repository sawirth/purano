$java = 'C:\Program Files\Java\jdk1.7.0_79\bin\java'
$puranoCP = 'C:\Users\Sandro\Documents\GitHub\purano\out\production\purano;C:\Users\Sandro\Documents\GitHub\purano\lib\*;'
$purano = 'jp.ac.osakau.farseerfc.purano.reflect.ClassFinder'

$homeDir = 'C:\Users\Sandro\Documents\GitHub\tomcat\output\classes\'

$userCP = $homeDir #+ '\*'
$pkg = 'org.apache.tomcat'
$cp = $puranoCP + $userCP
$resultFile = $homeDir + 'purano-result.txt'

& $java -cp $cp $purano $pkg | Out-File $resultFile