call npm install
call mvn clean -f "src\cat\pom.xml"
call mvn package -f "src\cat\pom.xml"
move "src\cat\target\cat-1.0.jar" src\cat.jar
del "src\cat\dependency-reduced-pom.xml"
PAUSE