rm -rf build
gradle wrapper
./gradlew fatJar
mkdir -p ./build/libs/testfiles && cp src/main/groovy/testfiles/* ./build/libs/testfiles