package com.github.p2m2.facade

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import utest.{assert, _}

import scala.language.implicitConversions
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object QueryEngineTest extends TestSuite {
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

      new QueryEngine().queryBindings(
        "SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
        context = QueryEngineOptions(sources = List(initStore())))
        .toFuture onComplete {
        case Success(results) =>
          results.on("data", (v: Bindings) => {
            assert(v.has("s"))
            assert(v.get("s") == new NamedNode("a"))
            assert(v.get("p") == new NamedNode("b"))
            assert(v.get("o") == new NamedNode("http://dbpedia.org/resource/Belgium"))

          })

        case Failure(t) =>
          println("An error has occurred: " + t.getMessage)
          println(t.getStackTrace.foreach(println _))
          assert(false)
      }
    }
      test("newEngine bindings - N3Store - Construct - quads ") {

        new QueryEngine().queryQuads("CONSTRUCT WHERE { ?s ?p ?o  } LIMIT 100",
          QueryEngineOptions(sources = List(initStore())))
          .toFuture onComplete {
          case Success(results) => {
            results.on("data", (v : Quad) => {
              assert(v.subject.value == "a")
              assert(v.predicate.value == "b")
            })
          }
          case Failure(t) =>
            println("An error has occurred: " + t.getMessage)
            println(t.getStackTrace.foreach( println _ ))
            assert(false)
        }
      }
      /*
           test("Serializing to a specific result format") {

             new QueryEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
               QueryEngineOptions(sources = List(initStore())))
               .toFuture onComplete {
               case Success(results: IQueryResult) => {
                 val data = new QueryEngine().resultToString(results,"application/sparql-results+json")
                 data.toFuture onComplete {
                   case Success(r) => r.data.pipe( stdout )
                   case Failure(t) => println("message :"+t)
                 }
               }
               case Failure(t) => println("An error has occurred: " + t.getMessage)
             }
           }

                 test("Serializing to a specific result format 2 ") {

                   new QueryEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
                     QueryEngineOptions(sources = List(initStore())))
                     .toFuture onComplete {
                     case Success(results: IQueryResult) => {
                       val data = new QueryEngine().resultToString(results,"application/sparql-results+json")
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

                   new QueryEngine().query("SELECT * {  ?s ?p ?o . VALUES ?o { <http://dbpedia.org/resource/Belgium> } . } LIMIT 100",
                     QueryEngineOptions(sources = List(initStore())))
                     .toFuture onComplete {
                     case Success(results: IQueryResult) => {
                       new QueryEngine().resultToString(results,"application/sparql-results+json")
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
*/

            test("newEngine bindings - SOURCES = List(N3Store + hypermedia) ") {
              new QueryEngine().queryBindings("SELECT ?s {  ?s ?p ?o . } LIMIT 100",
                QueryEngineOptions(
                  sources =
                    List(initStore(),
                      SourceDefinitionNewQueryEngine(
                        `type`= SourceType.hypermedia,value="https://fragments.dbpedia.org/2016-04/en"))))
                .toFuture onComplete {
                case Success(results) =>

                  println("  = N3Store + file =" )
                  println(results)

                  results.on("data", (v : Bindings) => {
                    println("test....................")
                    println("?s store+hypermedia ->" );
                    println(v.toString)
                  }).on("end", () => {
                    println(" ======== FIN store+hypermedia ============== ")
                  })

                case Failure(t) =>
                  println("An error has occurred: " + t.getMessage)
                  println(t.getStackTrace.foreach( println _ ))
                  assert(false)
              }
            }
    }
}
