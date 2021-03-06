/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package org.vertx.java.tests.core.json;

import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.testframework.TestBase;

import java.util.Iterator;

/**
 *
 * TODO complete testing!!
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JavaJsonTest extends TestBase {

  private static final Logger log = LoggerFactory.getLogger(JavaJsonTest.class);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testJsonObject() throws Exception {
    JsonObject obj = new JsonObject().putString("foo", "bar");
    String str = obj.encode();
    JsonObject obj2 = new JsonObject(str);
    assertEquals("bar", obj2.getString("foo"));
  }

  @Test
  public void testJsonArrayToClone() {
    JsonArray array = new JsonArray();
    array.add("test");
    JsonObject object = new JsonObject();
    object.putArray("array", array);

    //want to clone
    JsonObject object2 = new JsonObject(object.toMap());
    //this shouldn't throw an exception, it does before patch
    JsonArray array2 = object2.getArray("array");
  }

  @Test
  public void testRetrieveArrayItemByIndex() {
    JsonArray arr = new JsonArray();
    
    arr.addString("foo");
    arr.addObject(new JsonObject().putString("bar", "baz"));
    arr.addString("bap");
    
    assertEquals("baz", ((JsonObject) arr.get(1)).getString("bar"));
  }

  @Test
  public void testPutsNullObjectWithoutException() {
    log.debug(
      new JsonObject()
        .putObject("null", null) // this shouldn't cause a NullPointerException
        .encode()
    );
    
    log.debug(
      new JsonObject()
        .putObject("null", new JsonObject().putString("foo", "bar"))
        .encode()
    );
  }
  
  @Test
  public void testJsonElementTypeCheck(){
    JsonElement objElement = new JsonObject().putString("foo", "bar");
    JsonElement arrayElement = new JsonArray().addString("foo");
    
    assertTrue(objElement.isObject());
    assertFalse(objElement.isArray());
    
    assertFalse(arrayElement.isObject());
    assertTrue(arrayElement.isArray());
  }
  
  @Test
  public void testJsonElementConversionWithoutException(){
    JsonElement objElement = new JsonObject().putString("foo", "bar");
    JsonElement arrayElement = new JsonArray().addString("foo");
    
    JsonObject retrievedObject = objElement.asObject();  
    JsonArray retrievedArray = arrayElement.asArray();
    
    log.debug(retrievedObject.encode());
    log.debug(retrievedArray.encode());
  }
  
  @Test
  public void testJsonElementConversionWithException(){
    JsonElement objElement = new JsonObject().putString("foo", "bar");

    try{
      objElement.asArray();        
    }catch(ClassCastException e){
      return;
    }

    fail("Coercing JsonElement(Object) into JsonArray did not throw a ClassCastException");
  }

  @Test
  public void testJsonElementConversionWithException2(){
    JsonElement arrayElement = new JsonArray().addString("foo");

    try{
      arrayElement.asObject();        
    }catch(ClassCastException e){
      return;
    }
    
    fail("Coercing JsonElement(Array) into JsonObject did not throw a ClassCastException");
  }
  
  
  @Test
  public void testInsertJsonElementIntoJsonObjectWithouException(){
    JsonObject objElement = new JsonObject().putString("foo", "bar");
    JsonArray arrayElement = new JsonArray().addString("foo");
    
    /* Insert an Object */
    log.debug(
      new JsonObject()
        .putElement("elementField", objElement)
        .encode()
    );

    /* Insert an Array */
    log.debug(
      new JsonObject()
        .putElement("elementField", arrayElement)
        .encode()
    );
  }
  
  @Test
  public void testRetrieveJsonElementFromJsonObject(){
    JsonObject objElement = new JsonObject().putString("foo", "bar");
    
    JsonObject tester = new JsonObject()
      .putElement("elementField", objElement);

    JsonElement testElement = tester.getElement("elementField");
    
    assertEquals(objElement.getString("foo"), testElement.asObject().getString("foo"));
  }
  
  @Test
  public void testRetrieveJsonElementFromJsonObject2(){
    JsonArray arrayElement = new JsonArray().addString("foo");
    
    JsonObject tester = new JsonObject()
      .putElement("elementField", arrayElement);

    JsonElement testElement = tester.getElement("elementField");
    
    assertEquals(arrayElement.get(0), testElement.asArray().get(0));  
  }
  
  @Test
  public void testRetrieveJsonElementFromJsonObjectWithException(){  
    JsonObject tester = new JsonObject()
      .putString("elementField", "foo");

    try{
      tester.getElement("elementField");
    }catch(ClassCastException e){
      return;
    }
    
    fail("Retrieving a field that is not a JsonElement did not throw ClassCastException");
  }
  
  @Test
  public void testInsertJsonElementIntoJsonArrayWithouException(){
    JsonObject objElement = new JsonObject().putString("foo", "bar");
    JsonArray arrayElement = new JsonArray().addString("foo");
    
    /* Insert an Object */
    log.debug(
      new JsonArray()
        .addElement(objElement)
        .encode()
    );

    /* Insert an Array */
    log.debug(
        new JsonArray()
          .addElement(arrayElement)
          .encode()
      );  
  }
  
  @Test
  public void testRetrieveJsonElementFromJsonArray(){
    JsonObject objElement = new JsonObject().putString("foo", "bar");
    
    /* Insert an Object */
    JsonArray tester = new JsonArray()
      .addElement(objElement);

    JsonElement testElement = (JsonElement)tester.get(0);
    
    assertEquals(objElement.getString("foo"), testElement.asObject().getString("foo"));
  }
  
  @Test
  public void testRetrieveJsonElementFromJsonArray2(){
    JsonArray arrayElement = new JsonArray().addString("foo");
    
    /* Insert an Object */
    JsonArray tester = new JsonArray()
    .addElement(arrayElement);

    JsonElement testElement = (JsonElement)tester.get(0);
    
    assertEquals(arrayElement.get(0), testElement.asArray().get(0));
  }

  @Test
  public void testJsonArraysOfJsonObjectsEquality() {
    JsonArray array1 = new JsonArray().addObject(new JsonObject("{ \"a\": \"b\" }"));
    JsonArray array2 = new JsonArray().addObject(new JsonObject("{ \"a\": \"b\" }"));

    assertEquals(array1, array2);
  }

  @Test
  public void testGetBinary() {
    JsonObject json = new JsonObject();
    assertNull(json.getBinary("binary"));
  }

  @Test
  public void testCreateJsonArrayFromArray() {
    Object[] numbers = new Integer[]{1, 2, 3};
    // Json is not immutable
    JsonArray json = new JsonArray(numbers);
    json.add(4);
    assertEquals(4, json.size());
  }

  @Test
  public void testCreateJsonArrayFromArray2() {
    Object[] numbers = new Integer[] {1, 2, 3};
    // Json is not immutable
    JsonArray json = new JsonArray(numbers);
    try {
      Iterator i = json.iterator();
      i.next();
      i.remove();
    } catch (UnsupportedOperationException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testContainsField() {
    JsonObject obj = new JsonObject().putString("s", "bar");
    assertTrue(obj.containsField("s"));
    assertFalse(obj.containsField("t"));
  }
}
