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
import es.weso.rdf.jena.RDFAsJenaModel

class MatchNodeLabelSpec
    extends FunSpec
    with ShaclValidator
    with Matchers
    with Checkers {

  describe("MatchNodeLabel") {
    it("Should match node with label (single)") {
      val rdf_str = """|@prefix : <http://example.org/>. 
                       |:x :a :y .""".stripMargin
      val rdf = RDFAsJenaModel.fromChars(rdf_str,"TURTLE").get
      val ex = IRI("http://example.org/")
      val node = ex.add("x")
      
      val shape1 = TripleConstraint(None, ex.add("a"), IRIKind, plus)
      val labelS = IRILabel(IRI("S"))
      val rule1 = Rule(
          label = labelS, 
          shapeDefinition = OpenShape(shape = shape1, inclPropSet = Set()),
          extensionCondition = NoActions)
      val schema = SHACLSchema(None, rules = Seq(rule1), start = None) 
      val ctx = Context(rdf,schema,Typing.emptyTyping,PrefixMaps.commonShacl,true)
      matchNodeLabel_shouldPass(node,labelS,ctx,false)
  }
  }
  
  
  def matchNodeLabel_shouldPass(
      node: RDFNode, 
      label: Label,
      ctx: Context,
      withTrace: Boolean = false): Unit = {
    val result: Result[ValidationState] = for {
      _ <- setTrace(withTrace)
      r <- matchNodeLabel(node,label,ctx)
    } yield r
    
    if (result.isValid) {
      val sol1 = result.run.get
      val condition = sol1.filter(_.containsNodeLabel(node,label)).size > 0 
      if (!condition) {
        info("Result doesn't contain type " + node + "/" + label)
        info("Result: " + sol1.toList)
      }
      condition should be(true)
    } else {
      info("matchingNodeLabel: " + node + " with " + label)
      fail("Result: " + result + " is not valid")
    }
  }
  
  def fails_matchTriplesShapes(
      ts: Set[RDFTriple], 
      shape: ShapeExpr, 
      expectedState: ValidationState,
      withTrace: Boolean = false): Unit = {
    val ctx = Context.emptyContext
    val result: Result[ValidationState] = for {
      _ <- setTrace(withTrace)
      r <- matchTriplesShapeExpr(ts,shape,ctx)
    } yield r
    if (result.isValid) {
      info("Result valid but should fail")
      info("Result: " + result)
    }
    result.isFailure should be(true)
  }
  
}

