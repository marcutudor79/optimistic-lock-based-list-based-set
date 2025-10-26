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
sudo apt-get install make
sudo apt-get install git-lfs
```
2. Pull with git lfs in order to populate deps/ folder
```
git lfs pull
```
3. Open a terminal in code/ folder and run
```
source env-setup.sh
```
4. Run make & make check, wait for success
```
Oct 26, 2025 2:37:07 PM org.deuce.transform.asm.Agent transformJar
INFO: Start tranlating source: optimistic-lock-based-list-based-set/deps/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
Oct 26, 2025 2:37:18 PM org.deuce.transform.asm.Agent transformJar
INFO: Closing source:/optimistic-lock-based-list-based-set/deps/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
```
5. Profit!

# Why is this project useful
It shows the differences in performance of different types of synchronization algorithms.

# People involved
[Elena](https://github.com/lena0097) and me.

# License
GPL-3.0 license
