.data
_newline: .asciiz "\n"
.align 2
.text
.globl main
main:
addiu $sp, $sp, -40
sw $ra, 36($sp)
sw $s0, 32($sp)
sw $s1, 28($sp)
sw $s2, 24($sp)
sw $t0, 20($sp)
li $t8, 0
move $t0, $t8
li $t8, 1
move $s1, $t8
li $t8, 0
move $s2, $t8
li $t8, 1
move $s0, $t8
li $t8, 1
move $t0, $t8
_L1:
move $t8, $s2
li $t9, 6
blt $t8, $t9, _L3
li $t8, 0
move $s0, $t8
j _L4
_L3:
li $t8, 1
move $s0, $t8
_L4:
move $t8, $s0
li $t9, 0
beq $t8, $t9, _L2
move $t8, $t0
move $t9, $s1
addu $v1, $t8, $t9
move $s0, $v1
move $t8, $s0
move $t0, $t8
move $t8, $s2
li $t9, 3
blt $t8, $t9, _L5
li $t8, 0
move $s0, $t8
j _L6
_L5:
li $t8, 1
move $s0, $t8
_L6:
move $t8, $s0
li $t9, 0
beq $t8, $t9, _L7
move $t8, $t0
move $t9, $s1
addu $v1, $t8, $t9
move $s0, $v1
move $t8, $s0
move $t0, $t8
j _L8
_L7:
move $t8, $t0
move $t9, $s1
subu $v1, $t8, $t9
move $s0, $v1
move $t8, $s0
move $t0, $t8
_L8:
move $t8, $t0
move $t9, $s1
addu $v1, $t8, $t9
move $s0, $v1
move $t8, $s0
move $t0, $t8
move $t8, $s2
li $t9, 1
addu $v1, $t8, $t9
move $s0, $v1
move $t8, $s0
move $s2, $t8
j _L1
_L2:
move $a0, $t0
li $v0, 1
syscall
li $v0, 11
li $a0, 10
syscall
lw $s0, 32($sp)
lw $s1, 28($sp)
lw $s2, 24($sp)
lw $t0, 20($sp)
lw $ra, 36($sp)
addiu $sp, $sp, 40
jr $ra
