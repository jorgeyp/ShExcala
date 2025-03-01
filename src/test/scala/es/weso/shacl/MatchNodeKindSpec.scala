package es.weso.shacl

import es.weso.shacl.Shacl._
import es.weso.rdfgraph.nodes._
import es.weso.rdfgraph.statements._
import es.weso.rdfgraph._
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalatest.prop.Checkers
import es.weso.rdf._
import es.weso.shacl.PREFIXES._
import es.weso.monads.Result._
import es.weso.monads._
import util._

class MatchNodeKindSpec
    extends FunSpec
    with ShaclValidator
    with Matchers
    with Checkers {

  describe("matchNodeKind") {
    it("Should validate type IRI") {
      val obj: RDFNode = IRI("a")
      val iri = IRIKind
      val ctx = Context.emptyContext
      matchNodeKind(obj, iri, ctx).run should be(Success(Stream(true)))
    }
    
    it("Should not validate type IRI with a BNode") {
      val obj: RDFNode = BNodeId("a")
      val iri = IRIKind
      val ctx = Context.emptyContext
      matchNodeKind(obj, iri, ctx).isValid should be(false)  
    }

    it("Should validate type BNode") {
      val obj: RDFNode = BNodeId("a")
      val iri = BNodeKind
      val ctx = Context.emptyContext
      matchNodeKind(obj, iri, ctx).isValid should be(true)
    }
  }
  
}

