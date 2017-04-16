package com.github.orajavac.coding2017.jvm.attr;

import com.github.orajavac.coding2017.jvm.clz.ClassFile;
import com.github.orajavac.coding2017.jvm.loader.ByteCodeIterator;

public class CodeAttr extends AttributeInfo{

	private int maxStack ;
	private int maxLocals ;
	private int codeLen ;
	private String code;
	public String getCode() {
		return code;
	}

	//private ByteCodeCommand[] cmds ;
	//public ByteCodeCommand[] getCmds() {
	//	return cmds;
	//}
	private LineNumberTable lineNumTable;
	private LocalVariableTable localVarTable;
	private StackMapTable stackMapTable;
	
	public CodeAttr(int attrNameIndex, int attrLen, int maxStack, int maxLocals, int codeLen,String code /*ByteCodeCommand[] cmds*/) {
		super(attrNameIndex, attrLen);
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.codeLen = codeLen;
		this.code = code;
		//this.cmds = cmds;
	}

	public void setLineNumberTable(LineNumberTable t) {
		this.lineNumTable = t;
	}

	public void setLocalVariableTable(LocalVariableTable t) {
		this.localVarTable = t;		
	}
	
	public static CodeAttr parse(ClassFile clzFile, ByteCodeIterator iter){
		int attrNameIndex = iter.nextU2ToInt();
		int attrLen = iter.nextU4ToInt();
		int maxStack = iter.nextU2ToInt();
		int maxLocals = iter.nextU2ToInt();
		int codeLen = iter.nextU4ToInt();
		String code = iter.nextUxToHexString(codeLen);
		CodeAttr codeAttr = new CodeAttr(attrNameIndex,attrLen,maxStack,maxLocals,codeLen,code);
		int exceptionTableLen = iter.nextU2ToInt();
		if (exceptionTableLen>0){
			String exTable = iter.nextUxToHexString(exceptionTableLen);
		}
		int subAttrCount = iter.nextU2ToInt();
		for (int x=1;x<=subAttrCount;x++){
			int subAttrIndex = iter.nextU2ToInt();
			String subAttrName = clzFile.getConstantPool().getUTF8String(subAttrIndex);
			iter.back(2);
			if(AttributeInfo.LINE_NUM_TABLE.equalsIgnoreCase(subAttrName)){
				LineNumberTable t = LineNumberTable.parse(iter);
				codeAttr.setLineNumberTable(t);
			}else if (AttributeInfo.LOCAL_VAR_TABLE.equalsIgnoreCase(subAttrName)){
				LocalVariableTable t = LocalVariableTable.parse(iter);
				codeAttr.setLocalVariableTable(t);
			}else if (AttributeInfo.STACK_MAP_TABLE.equalsIgnoreCase(subAttrName)){
				StackMapTable t = StackMapTable.parse(iter);
				codeAttr.setStackMapTable(t);
			}else{
				throw new RuntimeException("need code to process");
			}
		}
		return null;
	}
	private void setStackMapTable(StackMapTable t) {
		this.stackMapTable = t;
		
	}

}
