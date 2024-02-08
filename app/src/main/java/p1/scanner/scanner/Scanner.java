package p1.scanner.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * This is the file you will modify.
 */
public class Scanner {

  //------------------------------------------------------------
  // TODO: declare the HashMaps that you will use to store
  // your tables. Also declare the start state.
  //------------------------------------------------------------
  HashMap<Character, String> catMap = new HashMap<>();
  ArrayList<TableReader.Transition> transMap = new ArrayList<>();
  HashMap<String, String> tokenTable = new HashMap<>();
  //------------------------------------------------------------
  // TODO: build your tables in the constructor and implement
  // the get methods.
  //------------------------------------------------------------

  /**
   * Builds the tables needed for the scanner.
   */
  public Scanner(TableReader tableReader) {
    // TODO: starting with the skeleton code below, build the
    // classifer, transition and token type tables. You will need
    // to also implement the test functions below once you have your
    // tables built.

    // Build catMap, mapping a character to a category.
    for (TableReader.CharCat cat : tableReader.getClassifier()) {
      assert catMap != null;
      catMap.put(cat.getC(),cat.getCategory());
      System.out.println("Character " + cat.getC() + " is of category "
              + cat.getCategory());
    }

    // Build the transition table. Given a state and a character category,
    // give a new state.
    for (TableReader.Transition t : tableReader.getTransitions()) {
      assert transMap !=null;
      transMap.add(t);
      System.out.println(t.getFromStateName() + " -- " + t.getCategory()
              + " --> " + t.getToStateName());
    }

    // Build the token types table
    for (TableReader.TokenType tt : tableReader.getTokens()) {
      tokenTable.put(tt.getState(), tt.getType());
      System.out.println("State " + tt.getState()
              + " accepts with the lexeme being of type " + tt.getType());
    }

  }

  /**
   * Returns the category for c or "not in alphabet" if c has no category. Do not hardcode
   * this. That is, this function should have nothing more than a table lookup
   * or two. You should not have any character literals in here such as 'r' or '3'.
   */
  public String getCategory(Character c) {
    if(!catMap.containsKey(c)){
      return "not in alphabet";
    }
    return catMap.get(c);
  }

  /**
   * Returns the new state given a current state and category. This models
   * the transition table. Returns "error" if there is no transition.
   * Do not hardcode any state names or categories. You should have only
   * table lookups here.
   */
  public String getNewState(String state, String category) {
    for (TableReader.Transition t: transMap) {
      if(Objects.equals(t.getFromStateName(), state) && Objects.equals(t.getCategory(), category)){
        return t.getToStateName();
      }
    }
    return "error";
  }

  /**
   * Returns the type of token corresponding to a given state. If the state
   * is not accepting then return "error".
   * Do not hardcode any state names or token types.
   */
  public String getTokenType(String state) {
    if(tokenTable.containsKey(state)){
      return tokenTable.get(state);
    }
    return "error";
  }

  //------------------------------------------------------------
  // TODO: implement nextToken
  //------------------------------------------------------------

  /**
   * Return the next token or null if there's a lexical error.
   */
  public Token nextToken(ScanStream ss) {
    // TODO: get a single token. This is an implementation of the nextToken
    // algorithm given in class. You may *not* use TableReader in this
    // function. Return null if there is a lexical error.
    /*
    r42[eof]
    initial state= s0
    lexeme
    current char
    category
    stack = clear stack, push "bad"
    while state != error state:
      next char:
      lexeme = lexeme+char
      if state within accept states:
        clear stack
       push(state)
       assign character category
       assign state a category
     while state != in acceptance state and state != bad) do:
      state = pop()
      truncate lexeme
      go back to actual char
     if state is accept state
      return type of state
     else return invalid
     */
    char cur = ' ';
    String category = "";
    String state = "s0";
    StringBuilder lexeme= new StringBuilder();
    ArrayList<String> nextMap = new ArrayList<>();
    nextMap.clear();

    while (state != "") {
      cur = ss.next();
      lexeme.append(cur);
      if(getTokenType(state) != "error"){
          nextMap.clear();
      }
      nextMap.add(state);
      category = getCategory(cur);
      state = getNewState(state,category);
    }
    while(getTokenType(state) == "error" && !state.equals("error")){
      state = nextMap.remove(nextMap.size()-1);
      lexeme = new StringBuilder("");
      ss.rollback();
    }
    if(getTokenType(state) != "error"){
      return new Token(getTokenType(state), lexeme.toString());
    }else {
      return null;
    }
  }

}
