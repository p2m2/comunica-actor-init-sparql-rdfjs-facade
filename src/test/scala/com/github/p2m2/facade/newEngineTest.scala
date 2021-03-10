package com.github.p2m2.facade

import utest._

import scala.concurrent.ExecutionContext.Implicits.global
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

    test("newEngine bindings - N3Store ") {

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
