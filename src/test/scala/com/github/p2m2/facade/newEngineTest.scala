package com.github.p2m2.facade

import io.scalajs.nodejs.process.Process.stdout
import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.language.implicitConversions
import scala.scalajs.js
import scala.util.{Failure, Success}

object newEngineTest extends TestSuite {
  def initStore() : N3Store = {
    val store : N3Store = new N3.Store()

    store.addQuad(DataFactory.quad(DataFactory.namedNode("a"),
      DataFactory.namedNode("b"),
      DataFactory.namedNode("http://dbpedia.org/resource/Belgium")))
    store.addQuad(DataFactory.quad(DataFactory.namedNode("a"),
      DataFactory.namedNode("b"),
      DataFactory.namedNode("http://dbpedia.org/resource/Ghent")))
    store
  }
  val tests = Tests {

    test("newEngine bindings - N3Store - bindings ") {

      Comunica.newEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
        QueryEngineOptions(sources = List(initStore())))
        .toFuture onComplete {
        case Success(results: IQueryResult) => {
          results.bindings().toFuture.foreach(r => {
            r.map(sol => println(sol("?s").value))
            r.map(sol => println(sol("?p").value))
            r.map(sol => println(sol("?o").value))
          })
        }
        case Failure(t) => println("An error has occurred: " + t.getMessage)
      }

      test("newEngine bindings - N3Store - Construct - quads ") {

        Comunica.newEngine().query("CONSTRUCT WHERE { ?s ?p ?o  } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            results.quads().toFuture.foreach(r => {
              r.map(sol => println(sol.subject.value))
              r.map(sol => println(sol.predicate.value))
              r.map(sol => println(sol.`object`.value))
              r.map(sol => println(sol.graph.value))
            })
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }

      test("Serializing to a specific result format") {

        Comunica.newEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            val data = Comunica.newEngine().resultToString(results,"application/sparql-results+json")
            data.toFuture onComplete {
              case Success(r) => r.data.pipe( stdout )
              case Failure(t) => println("message :"+t)
            }
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }

      test("Serializing to a specific result format 2 ") {

        Comunica.newEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            val data = Comunica.newEngine().resultToString(results,"application/sparql-results+json")
            data.toFuture onComplete {
              case Success(r) => r.data.on( "data" , (chunk : js.Object) => {
                println("chunk :" + chunk.toString)
              } )
              case Failure(t) => println("message :"+t)
            }
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }

      test("Serializing to a specific result format 3 ") {

        Comunica.newEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            Comunica.newEngine().resultToString(results,"application/sparql-results+json")
              .toFuture.map( v => {
              val p = Promise[String]()
              var sparql_results = ""
              println("HELLO WORLD !!!!")
              v.data.on("data", (chunk: js.Object) => {
                println(chunk)
                sparql_results += chunk.toString
              }).on("end", () => {
                p success sparql_results
              }).on("error", (error: String) => {
                p failure js.JavaScriptException(error)
              })
              p.future
            }).recover(error => {
              throw js.JavaScriptException(error.toString)
            })
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }

      test("newEngine bindings - N3Store + file ") {

        Comunica.newEngine().query("SELECT ?s {  ?s ?p ?o . } LIMIT 100",
          QueryEngineOptions(
            sources = List(initStore(),SourceDefinitionNewQueryEngine(`type`= SourceType.hypermedia,"http://fragments.dbpedia.org/2016/en")),
            queryFormat = QueryFormat.sparql))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            println("  = N3Store + file =" )
            results.bindingsStream.on("data", (binding: js.Map[String, Term]) => {
              val undef = DataFactory.blankNode()
              println("test....................")
              println("?s store+hypermedia ->" + binding.getOrElse("?s", undef).value);
              println("?s store+hypermedia ->" +binding.getOrElse("?s", undef).termType);
            }).on("end", () => {
              println(" ======== FIN store+hypermedia ============== ")
            })
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }

      test("newEngine bindingsStream - N3Store ") {

        Comunica.newEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results: IQueryResult) => {
            results.bindingsStream.on("data", (binding: js.Map[String, Term]) => {
              val undef = DataFactory.blankNode()
              println(binding.getOrElse("?s", undef).value);
              println(binding.getOrElse("?s", undef).termType);
              println(binding.getOrElse("?p", undef).value);
              println(binding.getOrElse("?o", undef).value);
            }).on("end", () => {
              println(" ======== FIN ============== ")
            })
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }
    }
  }
}
