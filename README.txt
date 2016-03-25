An Lattice Gas Automata based weather simulator.

To build this project you will need maven, and java 8.

https://maven.apache.org/download.cgi
https://maven.apache.org/install.html

cd to weatherSimulator directory (this one) and do

mvn install

Then you can either use the three scripts
runDataFeed.sh
runQueryRunner.sh
runLab.sh

to run the code, or type the following:
java -cp target/weatherSimulator-1.0-SNAPSHOT.jar au.com.jc.weather.user.DataFeedRunner
java -cp target/weatherSimulator-1.0-SNAPSHOT.jar au.com.jc.weather.user.QueryRunner <your args here>
java -cp target/weatherSimulator-1.0-SNAPSHOT.jar au.com.jc.weather.lab.LatticeLab

Queryrunner takes some args, but it will let you know the format when you first run it.