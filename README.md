# TruFeed

TruFeed is a fast feed publish and subscribe service written in Java

## Runbook

In order to try out the service, please follow the steps:

* Make sure you have JDK/JRE 8 install and JAVA_HOME is correctly set.
* Make sure you have Gradle 4 installed and GRADLE_HOME is correctly set.
* Checkout or download the code to your desktop/computer: git clone https://github.com/hellowrakesh/trufeed.git
* cd to the root directory of the project ($ROOT): `cd trufeed`
* Update $ROOT/config.yaml if you wish to change any default properties (viz. storage root dir). You can also override the default path from command line while running the service.
* Build the service, run `./gradlew clean build`.
* Once the build finishes, run the command: `java -jar $ROOT/build/libs/trufeed-${version}.jar server config.yaml`. Here config.yaml is the location of the config file. You can also prefer to use it by running the command: `./gradlew run`. If you want to override any properties in default config.yaml, you can pass as command line argument while running the executable jar file, viz. `java -Ddw.storage.rootDir=${storage_dir} -jar build/libs/trufeed-1.0.0.jar server config.yaml`.
* After you run the command, you will see the server started.
* You are all set!!!

## REST Apis

Explore following REST Api to try out the service.

* Create a new feed:

POST: /feed

Payload:

```java
	{
		"name": "my first feed post here",
		"title": "title 01",
		"description": "description 01"
	}
```	

Response:
```java
	{
	    "createDate": 1506355004697,
	    "uuid": "43e4b8c1-4bb1-363e-a6fa-25fc94adb165",
	    "name": "my first feed post here",
	    "title": "title 01",
	    "description": "description 01"
	}
```

* Get all the feeds:

GET: /feed

Response:
```java
	[
	    {
	        "createDate": 1506370575000,
	        "uuid": "04fda720-c8d4-3d38-8531-43733edd02be",
	        "name": "name_2",
	        "title": "title 2",
	        "description": "description 2"
	    }
	]
```

* Create a new user:

POST: /user

Payload:
```java
	{
	  "userName": "rakeshsinha",
	  "firstName": "Rakesh",
	  "lastName": "Sinha"
	}
```

Response:
```java
	{
	    "createDate": 1506354979004,
	    "uuid": "d703a5f3-a1fa-3042-bc32-ff400bdd1ecb",
	    "userName": "rakeshsinha",
	    "firstName": "Rakesh",
	    "lastName": "Sinha"
	}
```

* Get user details:

GET: /user/{userUuid}

Response:
```java
	{
	    "createDate": 1506354979004,
	    "uuid": "d703a5f3-a1fa-3042-bc32-ff400bdd1ecb",
	    "userName": "rakeshsinha",
	    "firstName": "Rakesh",
	    "lastName": "Sinha"
	}
```

* Subscribe user to a feed:

PATCH: /user/{userUuid}/feed/{feedUuid}/subscribe

Payload: No Content

Response: 
```java
	true
```

* Unsubscribe user to a feed:

PATCH: /user/{userUuid}/feed/{feedUuid}/unsubscribe

Payload: No Content

Response: 
```java
true
```

* View list of feeds user is subscribed:

GET: /user/{userUuid}/feeds

Response: 
```java
	[
	    {
	        "createDate": 1506355004000,
	        "uuid": "43e4b8c1-4bb1-363e-a6fa-25fc94adb165",
	        "name": "my first feed post here",
	        "title": "title 01",
	        "description": "description 01"
	    }
	]
```

* Publish articles to the feeds:

POST: /feed/{feedUuid}/article

Payload: 
```java
	{
		"title": "article 1",
		"description": "description 1",
		"content": "content 1",
		"metadata": {},
		"author": "rakesh"
	}
```

Response:
```
	{
	    "createDate": 1506355026306,
	    "uuid": "a0cccbbe-9c68-4a8c-9427-0a9f93b1c46c",
	    "title": "article 1",
	    "description": "description 1",
	    "content": "content 1",
	    "metadata": {},
	    "author": "rakesh"
	}
```

* Get articles from the feed a user is subscribed (articles are sorted by latest published date at the starting):

GET: /user/{userUuid}/feeds/articles

Response:
```java
	[
	    {
	        "feedUuid": "43e4b8c1-4bb1-363e-a6fa-25fc94adb165",
	        "articles": [
	            {
	                "createDate": 1506355026000,
	                "uuid": "a0cccbbe-9c68-4a8c-9427-0a9f93b1c46c",
	                "title": "article 1",
	                "description": "description 1",
	                "content": "content 1",
	                "metadata": {},
	                "author": "rakesh"
	            }
	        ]
	    }
	]
```
		
## Design Considerations

* The system is designed to allow basic functionality for a user to register, publish articles to feed, subscribe/unsubscribe and get the articles from subscribed feed.
* System is agnostic to the type of articles published and users can add metadata for specific custom data.
* The system is designed to be fast, scalable, concurrent allowing multiple users to use the system.
* All the data is persisted and it is achieved through a simple file system based storage.
* Fllows standard RESTful paradigm with backward compatibility for future enhancements.

## Choice of technology & frameworks

* [Dropwizard](https://github.com/dropwizard/dropwizard) for the general scaffolding. Dropwizard provides basic infrastructure to build microservices in java using some of the best technologies available in Open Source.
* [Gradle](https://gradle.org/) for build and packaging, it provides flexibility as well as incremental builds for efficient development.
* [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) as the language as developer is comfortable with the language and also Oracle made a lot of improvements over the years making it one of the most robust, high-performance and platform language.

## Storage

The service implements a custom storage. Here are the design rationales:

* Minimum footprint and almost no overhead of features which are need useful for the problem as opposed to using an external database/storage system.
* Simple hierarical structure:
```java
	- root_dir
		- user
			- b4dd4e1b-568e-30e7-8cc1-a9ad3c7dadc0
				- user.meta
				- feeds
					- dba13d4f-ea58-30e2-855d-9a1991f80ae9
					- d9fe8f36-b4f2-3f3a-8129-e560e13bff87
					- ac345550-f15d-34f8-8953-0d1fcb478507
					- a4a63642-bb30-3094-bf23-cb6af12e7728
					- 9d7d704c-032c-3f53-aeaf-d31696d27f85
		- feed
			- 9643c46f-95c1-312a-abf7-fde38edc9f7c
				- feed.meta
				- articles
					- _part0
```
* Each entity in the system has a uuid, for user its hashed using userName and for feed it uses hash of feed name. For articles, uuids are auto generated.
* User: Adding a user in the system, creates a directory inside user root with the directory name as the uuid. Under the directory, a user.meta file is created which has a serialized json string of the user's information. Each line always has createDate and uuid and first two elements.
* Sample user.meta file contents:
```java
{"createDate":"2017-09-25 21:29:55","uuid":"d703a5f3-a1fa-3042-bc32-ff400bdd1ecb","userName":"rakeshsinha","firstName":"Rakesh","lastName":"Sinha"}
```
* If a user is subscribed to a feed, it creates a 0 byte file inside user/{userUuid}/feeds/{feedUuid}.
* Unsubscribe action removes the file from feeds dir.
* The rationale behind maintaining 0 byte file inside user object store is because feeds are independent and isolated to the consumers and mostly high through-put that user management. Any user management doesn't affect the actual content being created. It also isolates a users action to a smaller scope and allows future scalability to maintain content and users separately.
* Feeds are stored in the same way as user, with each feed creating a directory feedUuid and file feed.mata which is a json serialized string of the feed object, with createDate and uuid are first two elements in the line.
* Sample feed.meta file contents:
```java
{"createDate":"2017-09-25 21:08:57","uuid":"43e4b8c1-4bb1-363e-a6fa-25fc94adb165","name":"my first feed post here","title":"title 01","description":"description 01"}
```
* Articles: These are utmost important as this is the main content being generated in the system. The system makes an underline assumption that all articles once published remain "immutable" forever. They are stored inside articles sub-directory in file _part0. The first version creates a single file but as the system grows, published articles can be split across multiple files based on max file size. Each file is sorted by publish date and is stored as json representation of article object having createDate and uuid as the first two values.
* The current version doesn't maintain any indexes for the articles but future versions could create indexes and reverse indexes to lookup a small subset of the data by max articles and  times to allow faster lookup.
* Sample _part0 file contents:

```java
{"createDate":"2017-09-25 21:09:08","uuid":"df107c19-319b-4237-b4b6-193de29525a0","title":"article 1","description":"description 1","content":"content 1","metadata":{},"author":"rakesh"}
{"createDate":"2017-09-25 21:09:10","uuid":"0bf0d2ca-13d8-45a0-b2ba-1cbd4dedf6a3","title":"article 1","description":"description 1","content":"content 1","metadata":{},"author":"rakesh"}
{"createDate":"2017-09-25 21:09:11","uuid":"36dae2d6-8a65-43d6-8aeb-719d732d541d","title":"article 1","description":"description 1","content":"content 1","metadata":{},"author":"rakesh"}
```

* When fetching list of articles for a given feed, the articles are returned in the reverse order, where the lastest published article is on the top.
* All the dates are UTC.

# Code Structure

The code is primarily devided in following high level layers:
 - api: responsible for handling the request from the client, basic validation and delegates to service.
 - service: this is where all business logic goes. It is agnostic of the storage layer and only performs business logic.
 - repository: abstraction for storage and here it uses file system storage extending FileRepository. It extensively uses Java NIO2 package.
 - container: everything that has to do with the server to get initialized and started. This is where all bindings, engine, configurations are managed.
 
* [Guice](https://github.com/google/guice): The code uses Guice for dependency injection and bindings.
* [Parseq](https://github.com/linkedin/parseq): The code follows async task based execution with Parseq, where all service and repository interfaces return a future task with a promise, which is satisfied later. The execution plan is started in api layer and it provides a non-block async execution for various tasks, making it highly efficient. Its a non-blocking framework developed by [LinkedIn](https://www.linkedin.com) and support parallel task execution asynchonously.
* [Jackson](https://github.com/FasterXML/jackson): It is used for all JSON serialization and de-serialization.

The code has been foramtted using Gradle plugin for [Google Java Formatter](https://github.com/google/google-java-format).

# Future Enhancements

* The next step would be to fetch only a subset of the data, as needed by the user or based on the time. This can be achieved through RandomFileAccess and reading buffers in reverse direction.
* Another improvement could be to add length of each line to the end to allow reading bytes in chunks.
* A line index could be created with start and end for each file (based on the size) to allow index lookup using trivial search algorithms as binary search in the index and then inside the file to allow seeking to the exact location for reading.
* Enhance validations and error messages (exceptions).