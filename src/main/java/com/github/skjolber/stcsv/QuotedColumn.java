package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


public class QuotedColumn extends AbstractColumn {

	protected static int QUOTE = (int)'"'; // 34
	
	public QuotedColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
	}

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment) {

		Label quoted = ifAtChar(mv, QUOTE); // quoted
		Label plainEmpty = ifAtChar(mv, divider); // empty
	
		saveOffsetInStart(mv);
		
		doIncrementWhileNotEqualToDivider(mv, divider);
		
		writeValue(mv, subClassInternalName);
		
		Label endLabel;
		if(optional) {
			endLabel = plainEmpty;
		} else {
			endLabel = new Label();
		}
		
		mv.visitJumpInsn(GOTO, endLabel);
		
		if(!optional) {
			// handle plain empty value
			mv.visitLabel(plainEmpty);
			throwMappingException(mv);
		}
				
		// handle quoted
		mv.visitLabel(quoted);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
		mv.visitVarInsn(ISTORE, rangeIndex);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ISTORE, startIndex);
		Label l19 = new Label();
		mv.visitLabel(l19);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, QUOTE);
		Label l20 = new Label();
		mv.visitJumpInsn(IF_ICMPNE, l20);
		Label l21 = new Label();
		mv.visitLabel(l21);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(ICONST_1);
		mv.visitInsn(IADD);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, QUOTE);
		Label l22 = new Label();
		mv.visitJumpInsn(IF_ICMPEQ, l22);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		Label l24 = new Label();
		mv.visitJumpInsn(IF_ICMPLE, l24);
		
		writeValue(mv, subClassInternalName);

		Label l26;
		if(optional) {
			l26 = l24;
		} else {
			l26 = new Label();			
		}
		mv.visitLabel(l26);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, divider);
		mv.visitJumpInsn(IF_ICMPNE, l26);
		Label l27 = new Label();
		mv.visitLabel(l27);
		mv.visitJumpInsn(GOTO, endLabel);

		if(!optional) {
			mv.visitLabel(l24);
			throwMappingException(mv);
		}			
		
		mv.visitLabel(l22);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitInsn(ICONST_1);
		mv.visitInsn(IADD);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitInsn(ISUB);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitIincInsn(startIndex, 1);
		Label l31 = new Label();
		mv.visitJumpInsn(GOTO, l31);
		mv.visitLabel(l20);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 10);
		mv.visitJumpInsn(IF_ICMPNE, l31);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, rangeIndex);
		mv.visitJumpInsn(IF_ICMPNE, l31);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitInsn(ISUB);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ISTORE, rangeIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		Label l34 = new Label();
		mv.visitJumpInsn(IF_ICMPGT, l34);

		throwMappingException(mv);
		
		mv.visitLabel(l34);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, startIndex);
		mv.visitLabel(l31);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitJumpInsn(GOTO, l19);
		
		mv.visitLabel(endLabel);
		
		mv.visitIincInsn(currentOffsetIndex, increment);
	}

	
}