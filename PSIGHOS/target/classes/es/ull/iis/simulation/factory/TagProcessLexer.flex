/**
 * This class is a simple example lexer.
 */

package es.ull.iis.simulation.factory;
 
import java_cup.runtime.*;
import es.ull.iis.simulation.factory.*;
 
%%

%class TagProcessLexer
%public
%unicode
%cup

%eofval{
	return symbol(TagProcessSymbols.EOF, context);
%eofval}

%{
	int openTagCounter = 0;
	int debug = 0;
	String context = new String();

  private Symbol symbol(int type, String context) {
    return new Symbol(type, yyline, yycolumn, new SymbolValue(context));
  }
  private Symbol symbol(int type, String value, String context) {
    return new Symbol(type, yyline, yycolumn, new SymbolValue(value, context));
  }

	public void setContext(String newContext) {
 		context = newContext;
	}
	
	public void activateDebug() {
 		debug = 1;
	}

	public void desactivateDebug() {
 		debug = 0;
	}
 
%}

Activity = [aA] [0-9]+ "."
Simulation = [sS] "." 
ResourceType = RT [0-9]+ "." | rt [0-9]+ "." 
Resource = [rR] [0-9]+ "." 
ElementType = ET [0-9]+ "." | et [0-9]+ "." 
DinamicElement = "@" [eE] "."
DinamicElementType = ("@" et "." | "@" ET ".")

TotalActivities = "totalActivities"
TotalResourceTypes = "totalResourceTypes"
TotalResources = "totalResources"
TotalWorkGroups = "totalWorkGroups"
TotalElementTypes = "totalElementTypes"
InitialSimulationTime = "initialSimulationTime"
FinalSimulationTime = "finalSimulationTime"
Id = "id"
ActiveElements = "activeElements"
LastStart = "lastStart"
LastFinish = "lastFinish"
QueueSize = "queueSize"
AvailableResources = "availableResources"
CurrentTs = "currentTs"

VarView = "createdElements" | "executionTime" | "waitTime" | "executionCounter" | "requestCounter" | "currentElements" | "availabilityTime" | "cancelTime" | "unavailabilityUse" | "cancelUse"

ActivityParam = [aA][0-9]+
SimulationParam = [sS]
ResourceParam = [rR][0-9]+
ResourceTypeParam = rt [0-9]+ | RT [0-9]+
ElementTypeParam = (ET | et) [0-9]+
DinamicElementParam = "@" [eE]
DinamicElementTypeParam = "@" (et | ET)
WorkGroupParam =  [aA][0-9]+"."WG[0-9]+

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \* + [^"/*"])*
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

StartTag  = "<%"
GetTag    = "GET"
SetTag    = "SET"
EndTag	  = "%>"

Value = [:jletterdigit:]*
Char = .

%%

<YYINITIAL> {

  {StartTag}	{
									openTagCounter++;
									if (debug == 1)
										System.out.println("STARTTAG: " + yytext());
									return symbol(TagProcessSymbols.STARTTAG, new String(yytext()), context);
								} 


  {GetTag}	{
									if (openTagCounter == 0) {
										if (debug == 1)
											System.out.println("CODE: " + yytext()); 
										return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
									} else {
										if (debug == 1)
											System.out.println("GETTAG: " + yytext());
										return symbol(TagProcessSymbols.GETTAG, new String(yytext()), context);
									}
								} 

  {SetTag}	{
									if (openTagCounter == 0) {
										if (debug == 1)
											System.out.println("CODE: " + yytext()); 
										return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
									} else {
										if (debug == 1)
											System.out.println("SETTAG: " + yytext());
										return symbol(TagProcessSymbols.SETTAG, new String(yytext()), context);
									}
								} 

  {EndTag}	{
							openTagCounter--;
							if (debug == 1)
								System.out.println("ENDTAG");
							return symbol(TagProcessSymbols.ENDTAG, new String(yytext()), context);
						} 

  /* simulation objects */
  {Activity}	{ 
								if (openTagCounter == 0) {
									if (debug == 1)
										System.out.println("CODE: " + yytext()); 
									return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
								} else {
									if (debug == 1)
										System.out.println("ACT: " + yytext());
  								return symbol(TagProcessSymbols.ACT, new String(yytext()), context); 
								}
  						}

  {Simulation}	{ 
									if (openTagCounter == 0) {
										if (debug == 1)
											System.out.println("CODE: " + yytext()); 
										return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
									} else {
										if (debug == 1)
											System.out.println("SIMUL:" + yytext());	
										return symbol(TagProcessSymbols.SIMUL, new String(yytext()), context); 	
									}
								}

  {ResourceType}	{
										if (openTagCounter == 0) {
											if (debug == 1)
												System.out.println("CODE: " + yytext()); 
											return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
										} else {
											if (debug == 1)
												System.out.println("RESTYPE:" + yytext());	
  										return symbol(TagProcessSymbols.RESTYPE, new String(yytext()), context); 
										}
  								}

  {Resource}	{
								if (openTagCounter == 0) {
									if (debug == 1)
										System.out.println("CODE: " + yytext()); 
									return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
								} else {
									if (debug == 1)
										System.out.println("RES: " + yytext());
  								return symbol(TagProcessSymbols.RES, new String(yytext()), context); 
								}
  						}

  {ElementType}	{
									if (openTagCounter == 0) {
										if (debug == 1)
											System.out.println("CODE: " + yytext()); 
										return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
									} else {
										if (debug == 1)
											System.out.println("ELEMTYPE: " + yytext());
  									return symbol(TagProcessSymbols.ELEMTYPE, new String(yytext()), context); 
									}
  							}

  {DinamicElement}	{ 
											if (openTagCounter == 0) {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
											} else {
												if (debug == 1)
													System.out.println("DINELEM: " + yytext());
												return symbol(TagProcessSymbols.DINELEM, new String(yytext()), context); 	
											}
										}

  {DinamicElementType}	{ 
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("DINELEMENTTYPE: " + yytext()); 
														return symbol(TagProcessSymbols.DINELEMTYPE, new String(yytext()), context); 
													}
												}
  {ActivityParam}	{ 
										if (openTagCounter == 0) {
											if (debug == 1)
												System.out.println("CODE: " + yytext()); 
											return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
										} else {
											if (debug == 1)
												System.out.println("ACTPARAM: " + yytext()); 
											return symbol(TagProcessSymbols.ACTPARAM, new String(yytext()), context); 
										}
									}
  {SimulationParam}	{ 
											if (openTagCounter == 0) {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
											} else {
												if (debug == 1)
													System.out.println("SIMULPARAM: " + yytext()); 
												return symbol(TagProcessSymbols.SIMULPARAM, new String(yytext()), context); 
											}
										}
  {ResourceTypeParam}	{ 
												if (openTagCounter == 0) {
													if (debug == 1)
														System.out.println("CODE: " + yytext()); 
													return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
												} else {
													if (debug == 1)
														System.out.println("RESTYPEPARAM: " + yytext()); 
													return symbol(TagProcessSymbols.RESTYPEPARAM, new String(yytext()), context); 
												}
											}
  {ResourceParam}	{ 
										if (openTagCounter == 0) {
											if (debug == 1)
												System.out.println("CODE: " + yytext()); 
											return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
										} else {
											if (debug == 1)
												System.out.println("RESPARAM: " + yytext()); 
											return symbol(TagProcessSymbols.RESPARAM, new String(yytext()), context); 
										}
									}
  {DinamicElementTypeParam}	{ 
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("DINELEMTYPEPARAM: " + yytext()); 
														return symbol(TagProcessSymbols.DINELEMTYPEPARAM, new String(yytext()), context); 
													}
												}
 {WorkGroupParam}	{ 
										if (openTagCounter == 0) {
											if (debug == 1)
												System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
										} else {
											if (debug == 1)
												System.out.println("WORKGROUPPARAM: " + yytext()); 
												return symbol(TagProcessSymbols.WGPARAM, new String(yytext()), context); 
											}
									}
  {ElementTypeParam}	{ 
												if (openTagCounter == 0) {
													if (debug == 1)
														System.out.println("CODE: " + yytext()); 
													return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
												} else {
													if (debug == 1)
														System.out.println("ELEMTYPEPARAM: " + yytext()); 
													return symbol(TagProcessSymbols.ELEMTYPEPARAM, new String(yytext()), context); 
												}
											}
  {DinamicElementParam}	{ 
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("DINELEMPARAM: " + yytext()); 
														return symbol(TagProcessSymbols.DINELEMPARAM, new String(yytext()), context); 
													}
												}
  {TotalActivities} {
											if (openTagCounter == 0) {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
											} else {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.TOTALACTIVITIES, new String(yytext()), context);
											}
										}
 
  {TotalResourceTypes} 	{
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("TOTALRESOURCETYPES: " + yytext()); 
														return symbol(TagProcessSymbols.TOTALRESOURCETYPES, new String(yytext()), context);
													}
												}

  {TotalResources} 	{
											if (openTagCounter == 0) {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
											} else {
												if (debug == 1)
													System.out.println("TOTALRESOURCES: " + yytext()); 
												return symbol(TagProcessSymbols.TOTALRESOURCES, new String(yytext()), context);
											}
										}

  {TotalWorkGroups} {
											if (openTagCounter == 0) {
												if (debug == 1)
													System.out.println("CODE: " + yytext()); 
												return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
											} else {
												if (debug == 1)
													System.out.println("TOTALWORKGROUPS: " + yytext()); 
												return symbol(TagProcessSymbols.TOTALWORKGROUPS, new String(yytext()), context);
											}
										}

  {TotalElementTypes} 	{
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("TOTALELEMENTTYPES: " + yytext()); 
														return symbol(TagProcessSymbols.TOTALELEMENTTYPES, new String(yytext()), context);
													}
												}

  {InitialSimulationTime} {
														if (openTagCounter == 0) {
															if (debug == 1)
																System.out.println("CODE: " + yytext()); 
															return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
														} else {
															if (debug == 1)
																System.out.println("INITIALSIMULATIONTIME: " + yytext()); 
															return symbol(TagProcessSymbols.INITIALSIMULATIONTIME, new String(yytext()), context);
														}
													}

  {FinalSimulationTime} {
													if (openTagCounter == 0) {
														if (debug == 1)
															System.out.println("CODE: " + yytext()); 
														return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
													} else {
														if (debug == 1)
															System.out.println("FINALSIMULATIONTIME: " + yytext()); 
														return symbol(TagProcessSymbols.FINALSIMULATIONTIME, new String(yytext()), context);
													}
												}

  {Id} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("ID: " + yytext()); 
						return symbol(TagProcessSymbols.ID, new String(yytext()), context);
					}
				}

  {ActiveElements} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("ACTIVEELEMENTS: " + yytext()); 
						return symbol(TagProcessSymbols.ACTIVEELEMENTS, new String(yytext()), context);
					}
				}

  {LastStart} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("LASTCODE: " + yytext()); 
						return symbol(TagProcessSymbols.LASTSTART, new String(yytext()), context);
					}
				}

  {LastFinish} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
							if (debug == 1)
								System.out.println("LASTFINISH: " + yytext()); 
						return symbol(TagProcessSymbols.LASTFINISH, new String(yytext()), context);
					}
				}

  {QueueSize} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("QUEUESIZE: " + yytext()); 
						return symbol(TagProcessSymbols.QUEUESIZE, new String(yytext()), context);
					}
				}

  {AvailableResources} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext());
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("AVAILABLERESOURCES: " + yytext());
						return symbol(TagProcessSymbols.AVAILABLERESOURCES, new String(yytext()), context);
					}
				}

  {CurrentTs} 	{
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext());
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("CURRENTTS: " + yytext());
						return symbol(TagProcessSymbols.CURRENTTS, new String(yytext()), context);
					}
				}

	{VarView} {
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("VARVIEW: " + yytext()); 
						return symbol(TagProcessSymbols.VARVIEW, new String(yytext()), context);
					}
	}

  "("		{ 
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE,new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("LPAREN: " + yytext()); 
						return symbol(TagProcessSymbols.LPAREN, new String(yytext()), context); 	
					}
				} 
  ")"		{ 
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("RPAREN: " + yytext()); 
						return symbol(TagProcessSymbols.RPAREN, new String(yytext()), context); 	
					}
				} 
  ","		{ 
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("COMMA: " + yytext()); 
						return symbol(TagProcessSymbols.COMMA, new String(yytext()), context); 	
					}
				} 

  "."		{ 
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
							System.out.println("DOT: " + yytext()); 
						return symbol(TagProcessSymbols.DOT, new String(yytext()), context); 	
					}
				} 

  {WhiteSpace}	{
									if (openTagCounter == 0) {
										if (debug == 1)
											System.out.println("CODE: " + yytext()); 
										return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
									} else {
										if (debug == 1)
											System.out.println("WHITESPACE: " + yytext()); 
										return symbol(TagProcessSymbols.SPACE, new String(yytext()), context);
									}
								}

  /* comments */
  {Comment}                     { /* ignore */ }

  {Value} {
						if (openTagCounter == 0) {
							if (debug == 1)
								System.out.println("CODE: " + yytext()); 
							return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
						} else {
							if (debug == 1)
								System.out.println("VALUE: " + yytext()); 
							return symbol(TagProcessSymbols.VALUE, new String(yytext()), context);
						}
					}
	{Char} {
					if (openTagCounter == 0) {
						if (debug == 1)
							System.out.println("CODE: " + yytext()); 
						return symbol(TagProcessSymbols.CODE, new String(yytext()), context);
					} else {
						if (debug == 1)
								System.out.println("CHAR: " + yytext());
						return symbol(TagProcessSymbols.CHAR, new String(yytext()), context);
					}
				 } 
}

