// Generated from src/main/antlr4/Query.g4 by ANTLR 4.7.1

package task_manager;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QueryVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QueryParser#chat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChat(QueryParser.ChatContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(QueryParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(QueryParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommand(QueryParser.CommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessage(QueryParser.MessageContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#emoticon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmoticon(QueryParser.EmoticonContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#link}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLink(QueryParser.LinkContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#color}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColor(QueryParser.ColorContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#mention}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMention(QueryParser.MentionContext ctx);
}