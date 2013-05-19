// Generated by delombok at Mon May 20 05:22:40 JST 2013
package jp.ac.osakau.farseerfc.purano.util;

import java.util.List;

public class MethodDesc {
	private final String returnType;
	private final List<String> arguments;
	
	@java.beans.ConstructorProperties({"returnType", "arguments"})
	@java.lang.SuppressWarnings("all")
	public MethodDesc(final String returnType, final List<String> arguments) {
		
		this.returnType = returnType;
		this.arguments = arguments;
	}
	
	@java.lang.SuppressWarnings("all")
	public String getReturnType() {
		return this.returnType;
	}
	
	@java.lang.SuppressWarnings("all")
	public List<String> getArguments() {
		return this.arguments;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof MethodDesc)) return false;
		final MethodDesc other = (MethodDesc)o;
		if (!other.canEqual((java.lang.Object)this)) return false;
		final java.lang.Object this$returnType = this.getReturnType();
		final java.lang.Object other$returnType = other.getReturnType();
		if (this$returnType == null ? other$returnType != null : !this$returnType.equals(other$returnType)) return false;
		final java.lang.Object this$arguments = this.getArguments();
		final java.lang.Object other$arguments = other.getArguments();
		if (this$arguments == null ? other$arguments != null : !this$arguments.equals(other$arguments)) return false;
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public boolean canEqual(final java.lang.Object other) {
		return other instanceof MethodDesc;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		final java.lang.Object $returnType = this.getReturnType();
		result = result * PRIME + ($returnType == null ? 0 : $returnType.hashCode());
		final java.lang.Object $arguments = this.getArguments();
		result = result * PRIME + ($arguments == null ? 0 : $arguments.hashCode());
		return result;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "MethodDesc(returnType=" + this.getReturnType() + ", arguments=" + this.getArguments() + ")";
	}
}