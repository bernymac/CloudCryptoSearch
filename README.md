# CloudCryptoSearch
CloudCryptoSearch is a searchable encryption middleware. It can be used to store, update, and search (through multiple keywords and returning ranked results)  encrypted text documents in a remote server or computational cloud. Both the privacy of the text documents and of the queries is guaranteed by the middleware.

A full description of this project was published here: https://dl.acm.org/citation.cfm?id=2491783&CFID=796115442&CFTOKEN=21280412 (alternate link: https://bernymac.github.io/assets/papers/oair13.pdf).

If you are to use this project, please cite:
Ferreira, B., & Domingos, H. (2013, May). Searching private data in a cloud encrypted domain. In Proceedings of the 10th Conference on Open Research Areas in Information Retrieval (pp. 165-172). C.I.D.


## Build Instructions
To compile this project, Java 1.7 (or higher) is required. The *lib* folder contains all dependencies of this project and should be included when compiling.

##Runing a Prototype
The project includes a simple prototype that can be executed to store and search *.txt* files in a cloud server.

Class **pt.unlfctdi.cryptosearch.cloud.CloudMain** is a prototype main class for the cloud server component of this project. If not changed, it will start the server at *localhost* (variable *host*) and will save files to directory */tmp/CryptoSearchCloud/* (variable *storage*). This class must be started before the client prototype.

The client prototype class is **pt.unlfctdi.cryptosearch.core.client.PrototypeClientApp**. It can be executed after the server has started, initializing the graphical application of the prototype. Inside it uses class **pt.unlfctdi.cryptosearch.core.client.PrototypeClientConnector**, which if not changed (variable *host*) will try to find the server at *localhost*. 