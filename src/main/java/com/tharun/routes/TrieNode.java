package com.tharun.routes;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TrieNode {
 private boolean isWord;
 private char content;
 private Map<Character,TrieNode> childNodes;
 
 public boolean isWord() {
  return isWord;
 }

 public void setWord(boolean isWord) {
  this.isWord = isWord;
 }

 public char getContent() {
  return content;
 }

 public void setContent(char content) {
  this.content = content;
 }
 
 public Map<Character, TrieNode> getChildNodes() {
  return childNodes;
 }

 public void setChildNodes(Map<Character, TrieNode> childNodes) {
  this.childNodes = childNodes;
 }

 public TrieNode(char _content) {
  content = _content;
  isWord = false;
  childNodes = new HashMap<Character,TrieNode>();
 }
 
 public TrieNode() {
  isWord = false;
  content = '$';
  childNodes = new HashMap<Character,TrieNode>();
 }

 public TrieNode getChildNode(char c) {
  if (childNodes != null) {
   return childNodes.get(c);
  }
  return null;
 }
 
 public boolean equals(Object o) {
  if (o instanceof TrieNode) {
   TrieNode node = (TrieNode)o;
   return (content == node.getContent());
  }
  return false;
 }
 
 public int hashCode() {
  return HashCodeBuilder.reflectionHashCode(this);
 }
}
