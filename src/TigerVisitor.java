// Generated from Tiger.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TigerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TigerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TigerParser#tiger_program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTiger_program(TigerParser.Tiger_programContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration_segment(TigerParser.Declaration_segmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#type_declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_declaration_list(TigerParser.Type_declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_declaration_list(TigerParser.Var_declaration_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#funct_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunct_list(TigerParser.Funct_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#type_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_declaration(TigerParser.Type_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(TigerParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_type(TigerParser.Base_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#var_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_declaration(TigerParser.Var_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#storage_class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStorage_class(TigerParser.Storage_classContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#id_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_list(TigerParser.Id_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#optional_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptional_init(TigerParser.Optional_initContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#funct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunct(TigerParser.FunctContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(TigerParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#param_list_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list_tail(TigerParser.Param_list_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#ret_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRet_type(TigerParser.Ret_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(TigerParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#stat_seq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_seq(TigerParser.Stat_seqContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(TigerParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#optreturn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptreturn(TigerParser.OptreturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#optprefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptprefix(TigerParser.OptprefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(TigerParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(TigerParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#expr_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_list(TigerParser.Expr_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#expr_list_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_list_tail(TigerParser.Expr_list_tailContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(TigerParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TigerParser#value_tail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_tail(TigerParser.Value_tailContext ctx);
}