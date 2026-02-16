# typescript-generator docs branch

This branch backs the GitHub pages for this repository.

## Steps to generate documentation for typescript-generator
```shell
version=4.0.0
git checkout main
mvn versions:set -DnewVersion=$version
mvn clean install -DskipTests && mvn site
mvn versions:revert
git checkout gh-pages
cp -r target/site/* --target-dir=maven
cp -r typescript-generator-core/target/site/* --target-dir=maven/typescript-generator-core
cp -r typescript-generator-maven-plugin/target/site/* --target-dir=maven/typescript-generator-maven-plugin
cp -r typescript-generator-spring/target/site/* --target-dir=maven/typescript-generator-spring
```