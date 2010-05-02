#!/bin/zsh
#CP=$PWD/lib/prefuse.jar:$PWD/lib/glazed*.jar
CP=$(grep -o "[^\"]*\.jar" .classpath | tr '\n' :)
echo CLASSPATH: $CP
javac=javac
classes=sbuild/classes
gensrc=src_generated
mkdir -p bin $classes $gensrc

# compile BWAnnotationProcessor
# stage2 compilation depends on stage1 annotation processing
$javac -Xjcov -cp $CP:$classes:bin \
    -s $gensrc \
    -d bin \
    -sourcepath src \
    $(ls -1 src/**/*.java | fgrep -v stage2)

# compile with annotation processing
$javac -Xjcov -cp $CP:$classes:bin \
    -processor annotations.stage1.process.BWAnnotationProcessor \
    -s $gensrc \
    -d bin \
    -sourcepath src \
    src/**/*.java &&
cd bin &&
jar -c -f ../staticproxy.jar **/*.class &&
cd .. &&
mv staticproxy.jar ../boolwidth-new/lib/

exit 0

# dump XML
$javac -Xjcov -cp $CP:$classes:bin \
    -processor annotations.stage2.process.XmlSourceDumper \
    -s $gensrc \
    -d bin \
    -Adumpxml \
    -sourcepath src \
    src/**/*.java $gensrc/**/*.java &&

