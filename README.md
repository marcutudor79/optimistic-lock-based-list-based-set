# optimistic-lock-based-list-based-set
This is a java project in which an optimistic lock-based list is implemented in Java and then tested using the synchrobench framework.

# Contents
- [How to run the project](how-to-run-this-project)
- [How to use the project](why-is-this-project-useful)
- [Credits](people-involved)
- [License](license)

# How to run this project
1. Install depdendencies
```
sudo apt-get install ant
sudo apt-get install make
sudo apt-get install openjdk-8-jdk-headless
```
2. Run the script from code
```
source env-setup.sh
```
3. Decompress deps/jdk-7u80-linux-x64.tar.gz and move it to /usr/lib/jvm
4. Run make in the code folder & wait for success
```
Success message:
INFO: Start tranlating source:/usr/lib/jvm/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
Oct 25, 2025 7:56:46 PM org.deuce.transform.asm.Agent transformJar
INFO: Closing source:/usr/lib/jvm/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
```
5. Profit!

# Why is this project useful
It shows the differences in performance of different types of synchronization algorithms.

# People involved
[Elena](https://github.com/lena0097) and me.

# License
GPL-3.0 license
