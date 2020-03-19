package wdl.transforms.biscayne.linking.expression

import cats.syntax.validated._
import common.validation.ErrorOr.ErrorOr
import wdl.model.draft3.elements.ExpressionElement
import wdl.model.draft3.elements.ExpressionElement._
import wdl.model.draft3.graph.expression.ValueEvaluator.ops._
import wdl.model.draft3.graph.expression.{EvaluatedValue, ValueEvaluator}
import wdl.transforms.base.linking.expression.values.BinaryOperatorEvaluators._
import wdl.transforms.base.linking.expression.values.EngineFunctionEvaluators._
import wdl.transforms.base.linking.expression.values.LiteralEvaluators._
import wdl.transforms.base.linking.expression.values.LookupEvaluators._
import wdl.transforms.base.linking.expression.values.TernaryIfEvaluator.ternaryIfEvaluator
import wdl.transforms.base.linking.expression.values.UnaryOperatorEvaluators._
import wdl.transforms.biscayne.linking.expression.values.BiscayneValueEvaluators._
import wom.expression.{ExpressionEvaluationOptions, IoFunctionSet}
import wom.values.WomValue
import wdl.transforms.base.wdlom2wdl.WdlWriter.ops._
import wdl.transforms.base.wdlom2wdl.WdlWriterImpl.expressionElementWriter

package object values {

  implicit val expressionEvaluator: ValueEvaluator[ExpressionElement] = new ValueEvaluator[ExpressionElement] {
    override def evaluateValue(a: ExpressionElement,
                               inputs: Map[String, WomValue],
                               ioFunctionSet: IoFunctionSet,
                               expressionEvaluationOptions: ExpressionEvaluationOptions)
                              (implicit expressionValueEvaluator: ValueEvaluator[ExpressionElement]): ErrorOr[EvaluatedValue[_ <: WomValue]] = {

      a match {
        // Literals:
        case a: PrimitiveLiteralExpressionElement => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: NoneLiteralElement.type => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: StringLiteral => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: StringExpression => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ObjectLiteral => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: MapLiteral => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ArrayLiteral => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: PairLiteral => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        // Lookups and member accesses:
        case a: IdentifierLookup => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ExpressionMemberAccess => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: IdentifierMemberAccess => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: IndexAccess => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        // Unary operators:
        case a: UnaryNegation => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: UnaryPlus => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: LogicalNot => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        // Binary operators (at some point we might want to split these into separate cases):
        case a: LogicalOr => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: LogicalAnd => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Equals => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: NotEquals => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: LessThan => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: LessThanOrEquals => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: GreaterThan => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: GreaterThanOrEquals => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Add => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Subtract => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Multiply => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Divide => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Remainder => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: TernaryIf => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        // Engine functions:
        case a: StdoutElement.type => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: StderrElement.type => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: ReadLines => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadTsv => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadMap => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadObject => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadObjects => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadJson => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadInt => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadString => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadFloat => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: ReadBoolean => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteLines => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteTsv => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteMap => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteObject => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteObjects => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: WriteJson => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Range => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Transpose => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Length => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Flatten => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Prefix => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: SelectFirst => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: SelectAll => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Defined => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Floor => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Ceil => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Round => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Glob => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: Size => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Basename => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: Zip => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Cross => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: Sub => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: Keys => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: AsMap => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: AsPairs => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: CollectByKey => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case a: Min => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)
        case a: Max => a.evaluateValue(inputs, ioFunctionSet, expressionEvaluationOptions)(expressionValueEvaluator)

        case other => s"Unable to process ${other.toWdlV1}: No evaluateValue exists for that type in WDL 1.1".invalidNel
      }
    }
  }
}
