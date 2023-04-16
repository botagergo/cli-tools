// Generated from src/main/antlr4/task_manager/Query.g4 by ANTLR 4.7.1

package task_manager;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryParser}.
 */
public interface QueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryParser#chat}.
	 * @param ctx the parse tree
	 */
	void enterChat(QueryParser.ChatContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#chat}.
	 * @param ctx the parse tree
	 */
	void exitChat(QueryParser.ChatContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(QueryParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(QueryParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(QueryParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(QueryParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(QueryParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(QueryParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(QueryParser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(QueryParser.MessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#emoticon}.
	 * @param ctx the parse tree
	 */
	void enterEmoticon(QueryParser.EmoticonContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#emoticon}.
	 * @param ctx the parse tree
	 */
	void exitEmoticon(QueryParser.EmoticonContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#link}.
	 * @param ctx the parse tree
	 */
	void enterLink(QueryParser.LinkContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#link}.
	 * @param ctx the parse tree
	 */
	void exitLink(QueryParser.LinkContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#color}.
	 * @param ctx the parse tree
	 */
	void enterColor(QueryParser.ColorContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#color}.
	 * @param ctx the parse tree
	 */
	void exitColor(QueryParser.ColorContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#mention}.
	 * @param ctx the parse tree
	 */
	void enterMention(QueryParser.MentionContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#mention}.
	 * @param ctx the parse tree
	 */
	void exitMention(QueryParser.MentionContext ctx);
}