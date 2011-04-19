export JAVA_HOME=/usr/java/j2sdk1.4.1_02
export CLASSPATH=lib/junit.jar
~/java/jakarta-ant-1.5.1/bin/ant -emacs -buildfile "$1" "$2"
