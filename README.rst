================
Play'R hackathon
================

This repository is dedicated to the Play'R hacking session.

It contains a merge of PlayR, PlayR-swagger and PlayR-mongo sources, and a sample application.

How to get it ?

.. code:: console

 $ git clone https://oleastre.be/git/PlayR-hack.git
 $ git submodule init
 $ git submodule update

After initial checkout, you should be able to import it in your favorite IDE.

And to run the hack application just start play as usual...

.. code:: console

 $ sbt run


Hackathon program
=================

We will start with a quick question and feed back session about current play'r code.

To follow up, there are two possible things to do, depending on your taste:

- Create a mongo based version of the tutorial application.
- Implement a DSL to simplify the routing definition.


Get familiar with play'r
------------------------

Having a look at the code before the start of the session will give us more time for interesting stuff.

Sample application
  
  The play'r source code contains a sample application (``PlayR/samples/playr-tutorial`` folder) that use almost all available features. 

  ReST API is accessible through http://localhost:9000/api/tutorial

  Swagger generated documentation is accessible through http://localhost:9000/api-docs

Play'r internals

  Two important parts to look at::

    PlayR/src/main/scala/twentysix/playr/core
    PlayR/src/main/scala/twentysix/playr/simple

  ``core`` contains the traits used by the play'r router. Those have to be implemented by 'drivers'.

  ``simple`` contains a basic ``core`` traits implementation.

  ``PlayR-mongo`` contains ``core`` traits implementation tailored for mongo based resources.


Mongo based tutorial application
--------------------------------

Goal ? 
  create a mongo based version of the existing tutorial application

Who ?  
  for those who want to learn how to use play'r

Play'R DSL
----------

Goal ? 
  get rid of direct instantiation of ``RestApiRouter``, ``RestResourceRouter``, ... 

Who ? 
  for those who already had a look at play'r internals and want to contribute


Current tutorial application api routing looks like this:

.. code:: scala

  object Application extends Controller {
  
    val crmApi = RestApiRouter()
      .add(PersonController)
      .add(new RestResourceRouter(CompanyController)
        .add("functions", GET, CompanyController.functions _)
        .add("employee", EmployeeController)
      )
  
    val api = RestApiRouter()
      .add(ColorController)
      .add("crm" -> crmApi)

    val apidocs = new SwaggerRestDocumentation(api)
  }

A first goal is to define a DSL syntax. To start the discussion, here is a sample

.. code:: scala

  object Application extends RestApi
                        with SwaggerDocumentation("/docs") {
    -> ColorController
    "crm" -> {
      -> PersonController
      -> CompanyController {
        "functions":: GET -> CompanyController.functions
        "employee" -> EmployeeController
      }
    }
  }

When we have a syntax, next goal is to implement it...