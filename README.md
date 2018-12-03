# MultiProbe-LSH
Efficient Indexing for HighDimensional Similarity Search

Lv, Qin, et al. "Multi-probe LSH: efficient indexing for high-dimensional similarity search." Proceedings of the 33rd international conference on Very large data bases. VLDB Endowment, 2007.

# Abstract for paper that this is based on:
Similarity indices for high-dimensional data are very desirable for building content-based search systems for feature rich data such as audio, images, videos, and other sensor data. Recently, locality sensitive hashing (LSH) and its variations have been proposed as indexing techniques for approximate similarity search. A significant drawback of these approaches is the requirement for a large number of hash tables in order to achieve good search quality. This paper proposes a new indexing scheme called multi-probe LSH that overcomes this drawback. Multi-probe LSH is built on the well-known LSH technique, but it intelligently probes
multiple buckets that are likely to contain query results in a hash table. Our method is inspired by and improves upon recent theoretical work on entropy-based LSH designed to reduce the space requirement of the basic LSH method. We have implemented the multi-probe LSH method and evaluated the implementation with two different high-dimensional datasets. Our evaluation shows that the multi-probe LSH method substantially improves upon previously proposed methods in both space and time efficiency. To achieve the same search quality, multi-probe LSH has a similar timeefficiency as the basic LSH method while reducing the number of hash tables by an order of magnitude. In comparison with the entropy-based LSH method, to achieve the same search quality, multi-probe LSH uses less query time and 5 to 8 times fewer number of hash tables.

## Known Limitations

The UI is known to be a bit slow. Optimizing for Swing or another Java framework didn't feel like an effective use of time for the assignment so we cut a bit of corners there. Please wait for the UI to respond
before trying to kill the process. In particular the loading of an index or the responsiveness of loading a directory when searching for files are known pain points. It can take many clicks to search into a
directory unfortunately, so if at first you don't succeed try again.