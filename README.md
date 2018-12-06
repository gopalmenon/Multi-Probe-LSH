# MultiProbe-LSH
Efficient Indexing for HighDimensional Similarity Search

Lv, Qin, et al. "Multi-probe LSH: efficient indexing for high-dimensional similarity search." Proceedings of the 33rd international conference on Very large data bases. VLDB Endowment, 2007.

# Abstract for paper that this is based on:
Similarity indices for high-dimensional data are very desirable for building content-based search systems for feature rich data such as audio, images, videos, and other sensor data. Recently, locality sensitive hashing (LSH) and its variations have been proposed as indexing techniques for approximate similarity search. A significant drawback of these approaches is the requirement for a large number of hash tables in order to achieve good search quality. This paper proposes a new indexing scheme called multi-probe LSH that overcomes this drawback. Multi-probe LSH is built on the well-known LSH technique, but it intelligently probes
multiple buckets that are likely to contain query results in a hash table. Our method is inspired by and improves upon recent theoretical work on entropy-based LSH designed to reduce the space requirement of the basic LSH method. We have implemented the multi-probe LSH method and evaluated the implementation with two different high-dimensional datasets. Our evaluation shows that the multi-probe LSH method substantially improves upon previously proposed methods in both space and time efficiency. To achieve the same search quality, multi-probe LSH has a similar timeefficiency as the basic LSH method while reducing the number of hash tables by an order of magnitude. In comparison with the entropy-based LSH method, to achieve the same search quality, multi-probe LSH uses less query time and 5 to 8 times fewer number of hash tables.

Requirements:
java 11.0.1 2018-10-16 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.1+13-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.1+13-LTS, mixed mode)
Gradle 4.10.2

Built on:
Windows 10 and
Mac OS X 10.13.6 x86_64

Built using Intellij (https://www.jetbrains.com/idea/)

The easiest way to run the application is by simply running the included uberjar in the repository.
Run this by running the command:
$ java -jar Multi-Probe-LSH-all.jar

This will bring up a swing dialogue which will have the contents of our application loaded in it.

You may also if you wish compile from source. This will be mostly undocumented and the expectation is that a Java developer with Gradle experience
can run the source code in their selected IDE with their needed build configuration.

An example to build from source and run with Gradle might look like:
$ gradle build
$ gradle run

The application runs searches by first building an index. Before building your index we recommend changing your settings to a configuration you would
like as the index once built cannot change its settings. You will have to rebuild an index to change its settings.

To build an index go to File -> Build Index. This will prompt a file open dialogue box.
The file the index expects is a tab delimited file of the format <id for image><tab><url for image>.
This will be parsed to download the images and then build an index of hash tables from them.

Examples of the format can be seen here:
https://github.com/illegalnumbers/multi-probe-lsh-data/blob/master/images/file_1

The format is the same format as the imagenet file format from which we were building our testing image indexes:
http://image-net.org/download

After creating your index with the specified settings you will be prompted to save your index.
We allow for the saving of the index only once so decide now if you would like to save your index. It is not required to save your index to search
from it and if you do not want to wait for the index to save to disk you can click cancel and it will still allow you to search.

If you save an index you can load it at a later time with the File -> Load Index option.

If you want you can also avoid the downloading and parsing of images by using the example predownloaded features file provided in the repo under
the file `featuresFromDaemon.set` and loading it with the option File -> Build Index from Saved Features. This will load approximately 8k image
features predownloaded into a new index with the settings you specify.

After building your index you can search for a file with the Search -> Search by Url or Search by Image File

These options will search based on the index selected configuration that you have entered to the file.

After doing a search you can compare it to a brute force baseline search by clicking Search -> Brute Force Search By URL
and then clicking Metrics -> Show Metrics to show the comparison between the brute force metrics and the search you have performed
with your index.

If you would like to change how many perturbation sets get applied when doing your searches on your index
you can select Search -> Set Perturbation Count.

To exit the application select File -> Exit.

## Known Limitations

The UI is known to be a bit slow. Optimizing for Swing or another Java framework didn't feel like an effective use of time for the assignment so we cut a bit of corners there. Please wait for the UI to respond
before trying to kill the process. In particular the loading of an index or the responsiveness of loading a directory when searching for files are known pain points. It can take many clicks to search into a
directory unfortunately, so if at first you don't succeed try again. The saving of indexes is very slow and somtimes the search can be a bit slow as well. Building large indexes is slow.