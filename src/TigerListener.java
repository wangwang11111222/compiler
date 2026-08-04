// Generated from Tiger.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TigerParser}.
 */
public interface TigerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TigerParser#tiger_program}.
	 * @param ctx the parse tree
	 */
	void enterTiger_program(TigerParser.Tiger_programContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#tiger_program}.
	 * @param ctx the parse tree
	 */
	void exitTiger_program(TigerParser.Tiger_programContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration_segment(TigerParser.Declaration_segmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration_segment(TigerParser.Declaration_segmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#type_declaration_list}.
	 * @param ctx the parse tree
	 */
	void enterType_declaration_list(TigerParser.Type_declaration_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#type_declaration_list}.
	 * @param ctx the parse tree
	 */
	void exitType_declaration_list(TigerParser.Type_declaration_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 */
	void enterVar_declaration_list(TigerParser.Var_declaration_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 */
	void exitVar_declaration_list(TigerParser.Var_declaration_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funct_list}.
	 * @param ctx the parse tree
	 */
	void enterFunct_list(TigerParser.Funct_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funct_list}.
	 * @param ctx the parse tree
	 */
	void exitFunct_list(TigerParser.Funct_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void enterType_declaration(TigerParser.Type_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void exitType_declaration(TigerParser.Type_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(TigerParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(TigerParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#base_type}.
	 * @param ctx the parse tree
	 */
	void enterBase_type(TigerParser.Base_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#base_type}.
	 * @param ctx the parse tree
	 */
	void exitBase_type(TigerParser.Base_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void enterVar_declaration(TigerParser.Var_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void exitVar_declaration(TigerParser.Var_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#storage_class}.
	 * @param ctx the parse tree
	 */
	void enterStorage_class(TigerParser.Storage_classContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#storage_class}.
	 * @param ctx the parse tree
	 */
	void exitStorage_class(TigerParser.Storage_classContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#id_list}.
	 * @param ctx the parse tree
	 */
	void enterId_list(TigerParser.Id_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#id_list}.
	 * @param ctx the parse tree
	 */
	void exitId_list(TigerParser.Id_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#optional_init}.
	 * @param ctx the parse tree
	 */
	void enterOptional_init(TigerParser.Optional_initContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#optional_init}.
	 * @param ctx the parse tree
	 */
	void exitOptional_init(TigerParser.Optional_initContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funct}.
	 * @param ctx the parse tree
	 */
	void enterFunct(TigerParser.FunctContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funct}.
	 * @param ctx the parse tree
	 */
	void exitFunct(TigerParser.FunctContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#param_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_list(TigerParser.Param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#param_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_list(TigerParser.Param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#param_list_tail}.
	 * @param ctx the parse tree
	 */
	void enterParam_list_tail(TigerParser.Param_list_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#param_list_tail}.
	 * @param ctx the parse tree
	 */
	void exitParam_list_tail(TigerParser.Param_list_tailContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#ret_type}.
	 * @param ctx the parse tree
	 */
	void enterRet_type(TigerParser.Ret_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#ret_type}.
	 * @param ctx the parse tree
	 */
	void exitRet_type(TigerParser.Ret_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(TigerParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(TigerParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#stat_seq}.
	 * @param ctx the parse tree
	 */
	void enterStat_seq(TigerParser.Stat_seqContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#stat_seq}.
	 * @param ctx the parse tree
	 */
	void exitStat_seq(TigerParser.Stat_seqContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(TigerParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(TigerParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#optreturn}.
	 * @param ctx the parse tree
	 */
	void enterOptreturn(TigerParser.OptreturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#optreturn}.
	 * @param ctx the parse tree
	 */
	void exitOptreturn(TigerParser.OptreturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#optprefix}.
	 * @param ctx the parse tree
	 */
	void enterOptprefix(TigerParser.OptprefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#optprefix}.
	 * @param ctx the parse tree
	 */
	void exitOptprefix(TigerParser.OptprefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(TigerParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(TigerParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(TigerParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(TigerParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#expr_list}.
	 * @param ctx the parse tree
	 */
	void enterExpr_list(TigerParser.Expr_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#expr_list}.
	 * @param ctx the parse tree
	 */
	void exitExpr_list(TigerParser.Expr_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#expr_list_tail}.
	 * @param ctx the parse tree
	 */
	void enterExpr_list_tail(TigerParser.Expr_list_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#expr_list_tail}.
	 * @param ctx the parse tree
	 */
	void exitExpr_list_tail(TigerParser.Expr_list_tailContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(TigerParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(TigerParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#value_tail}.
	 * @param ctx the parse tree
	 */
	void enterValue_tail(TigerParser.Value_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#value_tail}.
	 * @param ctx the parse tree
	 */
	void exitValue_tail(TigerParser.Value_tailContext ctx);
}