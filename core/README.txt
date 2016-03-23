To build this project you will need maven.

cd to core directory and do
mvn install

Then you can either use the three scripts
runDataFeed.sh
runQueryRunner.sh
runLab.sh

to run the code, or type the following:
java -cp target/lattice-1.0-SNAPSHOT.jar au.com.jc.weather.user.DataFeedRunner
java -cp target/lattice-1.0-SNAPSHOT.jar au.com.jc.weather.user.QueryRunner $@
java -cp target/lattice-1.0-SNAPSHOT.jar au.com.jc.weather.lab.LatticeLab

Queryrunner takes some args, but it will let you know.