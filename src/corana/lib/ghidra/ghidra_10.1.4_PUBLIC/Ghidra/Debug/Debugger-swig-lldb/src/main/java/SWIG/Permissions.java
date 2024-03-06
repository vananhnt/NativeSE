/* ###
 * IP: Apache License 2.0 with LLVM Exceptions
 */
/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package SWIG;

public final class Permissions {
  public final static Permissions ePermissionsWritable = new Permissions("ePermissionsWritable", lldbJNI.ePermissionsWritable_get());
  public final static Permissions ePermissionsReadable = new Permissions("ePermissionsReadable", lldbJNI.ePermissionsReadable_get());
  public final static Permissions ePermissionsExecutable = new Permissions("ePermissionsExecutable", lldbJNI.ePermissionsExecutable_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static Permissions swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + Permissions.class + " with value " + swigValue);
  }

  private Permissions(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private Permissions(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private Permissions(String swigName, Permissions swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static Permissions[] swigValues = { ePermissionsWritable, ePermissionsReadable, ePermissionsExecutable };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

