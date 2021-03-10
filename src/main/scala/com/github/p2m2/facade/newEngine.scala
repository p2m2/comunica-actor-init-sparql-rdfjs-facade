/**
 * olivier.filangi@inrae.fr - P2M2 Platform - https://github.com/p2m2
 */
package com.github.p2m2.facade

import com.github.p2m2.facade.QueryFormat.QueryFormat
import com.github.p2m2.facade.SourceType.SourceType
import io.scalajs.nodejs.stream

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

object Comunica {
  @js.native
  @JSImport("@comunica/actor-init-sparql", "newEngine")
  def newEngine() : ActorInitSparql = js.native

  @js.native
  @JSImport("@comunica/actor-init-sparql", "newEngineDynamic")
  def newEngineDynamic(config : js.Object) : js.Promise[ActorInitSparql] = js.native

}

@js.native
//@JSImport("@comunica/actor-init-sparql/lib/ActorInitSparql", "ActorInitSparql-browser")
@JSImport("@comunica/actor-init-sparql/lib/ActorInitSparql", "ActorInitSparql")
class ActorInitSparql extends js.Object {
  def invalidateHttpCache(url : String = null) : Unit = js.native
  def query( request : String , context : QueryEngineOptions = null) : js.Promise[IQueryResult] = js.native
  def resultToString(queryResult : IQueryResult , mediaType: String ) : js.Promise[IActorSparqlSerializeOutput] = js.native
}

/**
 *  IQueryResultBindings,
  IQueryResultQuads,
  IQueryResultBoolean,
 */
@js.native
@JSImport("@comunica/actor-init-sparql", "IQueryResult")
class IQueryResult extends js.Object {
  //type,bindingsStream,metadata,variables,canContainUndefs,bindings,context
  //val booleanResult : Boolean = js.native /* ask */
  val `type` : String = js.native //bindings
  def metadata() : js.Any = js.native
  val variables : js.Array[String] = js.native
  val context : js.Object = js.native
  val bindingsStream : stream.Transform = js.native
  def bindings() : js.Promise[js.Array[js.Map[String,Term]]] = js.native
  def quads() : js.Promise[js.Array[Quad]] = js.native
  //val quadStream : stream.Transform  = js.native
}

@js.native
@JSImport("@comunica/actor-init-sparql", "IActorSparqlSerializeOutput")
class IActorSparqlSerializeOutput extends js.Object {
  val data : stream.Readable = js.native
}

/**
 * https://comunica.dev/docs/query/advanced/context/
 */
trait QueryEngineOptions extends js.Object {
  val sources                : js.UndefOr[js.Array[String | SourceDefinitionNewQueryEngine | N3.Store]] = js.undefined
  val lenient                : js.UndefOr[Boolean] = js.undefined
  val initialBindings        : js.UndefOr[Bindings] = js.undefined
  val baseIRI                : js.UndefOr[String] = js.undefined
  val date                   : js.UndefOr[js.Date] = js.undefined
  val httpIncludeCredentials : js.UndefOr[Boolean] = js.undefined
  val httpProxyHandler       : js.UndefOr[ProxyHandlerStatic] = js.undefined
  val httpAuth               : js.UndefOr[String] = js.undefined /* 'username:password' */
  val log                    : js.UndefOr[LoggerPretty] = js.undefined
  val queryFormat            : js.UndefOr[QueryFormat.Value] = js.undefined
}

object QueryEngineOptions {
  def apply(
             sources                : List[String | SourceDefinitionNewQueryEngine | N3.Store] = List(),
             lenient                : js.UndefOr[Boolean] = js.undefined,
             initialBindings        : js.UndefOr[Bindings] = js.undefined,
             baseIRI                : js.UndefOr[String] = js.undefined,
             date                   : js.UndefOr[js.Date] = js.undefined,
             httpIncludeCredentials : js.UndefOr[Boolean] = js.undefined,
             httpProxyHandler       : js.UndefOr[ProxyHandlerStatic] = js.undefined,
             httpAuth               : js.UndefOr[String] = js.undefined,
             log                    : js.UndefOr[LoggerPretty] = js.undefined,
             queryFormat            : QueryFormat.Value = null
           ): QueryEngineOptions = js.Dynamic.literal(
    "sources" -> (sources match {
      case l if l.length>0  => l.toJSArray
      case _ => js.undefined
    }),
    "lenient" ->  lenient,
    "initialBindings" ->  initialBindings,
    "baseIRI" -> baseIRI,
    "date" -> date,
    "httpIncludeCredentials" ->  httpIncludeCredentials,
    "httpProxyHandler" ->  httpProxyHandler,
    "httpAuth" ->  httpAuth,
    "log" ->  log,
    "queryFormat" ->  (queryFormat match {
      case s : QueryFormat => s.toString
      case null => js.undefined
    })
  ).asInstanceOf[QueryEngineOptions]
}

trait SourceDefinitionNewQueryEngine extends js.Object {
  val `type`            : js.UndefOr[SourceType.Value] = js.undefined
  val value            : js.UndefOr[String] = js.undefined
}

object SourceDefinitionNewQueryEngine {
  def apply(
             `type` : SourceType,
             value : String
           ) : SourceDefinitionNewQueryEngine = js.Dynamic.literal(
    `type` = `type`.toString,
    value = value
  ).asInstanceOf[SourceDefinitionNewQueryEngine]
}


object SourceType extends Enumeration {
  type SourceType = Value
  val hypermedia, file, sparql, rdfjsSource, hdtFile, ostrichFile = Value
}

object QueryFormat extends Enumeration {
  type QueryFormat = Value
  val sparql,graphql = Value
}

object ResultFormat extends Enumeration {
  type ResultFormat = Value
  val `application/json`,simple,`application/sparql-results+json`,`application/sparql-results+xml`,
  `text/csv`,`text/tab-separated-values`,stats,table, tree, `application/trig`, `application/n-quads`, `text/turtle`,
  `application/n-triples`, `text/n3`, `application/ld+json` = Value
}


@js.native
@JSImport("@comunica/bus-query-operation", "Bindings")
class Bindings(options : js.Object) extends js.Object
/*
new Bindings({
    '?template1': factory.literal('Value1'),
    '?template2': factory.literal('Value2'),
  })
 */

@js.native
@JSImport("@comunica/logger-pretty", "LoggerPretty")
class LoggerPretty(options : js.Object) extends js.Object

@js.native
@JSImport("@comunica/actor-http-proxy", "ProxyHandlerStatic")
class ProxyHandlerStatic(uri : String ) extends js.Object

/* new LoggerPretty({ level: 'debug' }) */
