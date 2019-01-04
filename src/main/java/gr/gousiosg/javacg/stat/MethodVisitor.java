/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gr.gousiosg.javacg.stat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

/**
 * The simplest of method visitors, prints any invoked method signature for all
 * method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

	private MethodGen mg;
	private ConstantPoolGen cp;
	private Set<String> outboundMethods = new HashSet<>();
	private String format;

	public MethodVisitor(MethodGen m, JavaClass jc) {
		mg = m;
		cp = mg.getConstantPool();
		format = "%s:%s(%s)";
	}

	public static String argumentList(Type[] arguments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(arguments[i].toString());
		}
		return sb.toString();
	}

	public Set<String> start() {
		if (mg.isAbstract() || mg.isNative())
			return Collections.emptySet();

		for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
			Instruction i = ih.getInstruction();

			if (!visitInstruction(i))
				i.accept(this);
		}
		
		boolean removePrimitives = Boolean.parseBoolean(System.getProperty("removePrimitives"));
		
		if(removePrimitives) {
			return outboundMethods.stream().filter(method -> {return !method.startsWith("java");}).collect(Collectors.toSet());	
		}else {
			return outboundMethods;
		}
		
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		outboundMethods.add(String.format(format, i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		outboundMethods.add(String.format(format, i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		outboundMethods.add(String.format(format, i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		outboundMethods.add(String.format(format, i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
		outboundMethods.add(String.format(format, i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}
}
