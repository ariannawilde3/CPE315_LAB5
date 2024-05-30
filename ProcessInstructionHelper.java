public class ProcessInstructionHelper {
    public ProcessInstructionHelper() {

    }
    
    static public boolean detectDataHazard(Instruction idStage, Instruction ifStage) {
        boolean hazard = false;
        if (ifStage == null || idStage == null) return false;

        if(idStage.getOperationString().equals("lw") && ifStage.getOperationString().equals("lw") )
        {
            return false;
        }
        
        String ifSource = ifStage.getSource();
        String ifTarget = ifStage.getTarget();        
        //String ifDest = ifStage.getDest();
        if ((idStage.getTarget() != null && idStage.getTarget().equals(ifSource)) || 
            (idStage.getTarget() != null && idStage.getTarget().equals(ifTarget))) {
            // ||
            // (idStage.getSource() != null && idStage.getSource().equals(ifSource)) || 
            // (idStage.getSource() != null && idStage.getSource().equals(ifTarget))) {
            hazard = true;
        }
        return hazard;
    }
   
    // return true if pc is moved
    public static int ProcessInstruction(Instruction instruction) {                  
            int sourceIndex = 0;
            int targetIndex = 0;
            int destIndex = 0;
            int immediate = 0;
            int memoryAddress = 0;
            int movePcTo = -1;

            lab5.totalInstructions++;
            switch(instruction.getOperationString()){

                case "and":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab5.Registers[destIndex] = lab5.Registers[sourceIndex] & lab5.Registers[targetIndex];

                    break;

                case "or":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
        
                    lab5.Registers[destIndex] = lab5.Registers[sourceIndex] | lab5.Registers[targetIndex];

                    break;

                case "add":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                    
                    lab5.Registers[destIndex] = lab5.Registers[sourceIndex] + lab5.Registers[targetIndex];

                    break;

                case "addi":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    lab5.Registers[targetIndex] = lab5.Registers[sourceIndex] + immediate;

                    break;

                case "sll":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab5.Registers[destIndex] = lab5.Registers[sourceIndex] << immediate;

                    break;

                case "sub":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                
                    lab5.Registers[destIndex] = lab5.Registers[sourceIndex] - lab5.Registers[targetIndex];

                    break;

                case "slt":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);

                    if (lab5.Registers[sourceIndex] < lab5.Registers[targetIndex]) {
                        lab5.Registers[destIndex] = 1;
                    } else {
                        lab5.Registers[destIndex] = 0;
                    }

                    break;

                case "beq":
                    lab5.numPreds++;

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);            
            
                    Boolean prediction = lab5.getPrediction();
                    if (lab5.Registers[sourceIndex] == lab5.Registers[targetIndex]) {
                        if (prediction) {
                            lab5.correctPreds++;
                        }
                        lab5.updatePrediction(true);
                        movePcTo =  getLabelAddr(instruction.getLabelName());                        
                    } else {
                        if (!prediction) {
                            lab5.correctPreds++;
                        }

                        lab5.updatePrediction(false);
                    }

                    break;

                case "bne":
                    lab5.numPreds++;

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
            
                    // if(R[rs]!=R[rt])
                    // PC=PC+1+BranchAddr
                    Boolean pred = lab5.getPrediction();

                    if (lab5.Registers[sourceIndex] != lab5.Registers[targetIndex]) {
                        if (pred) {
                            lab5.correctPreds++;
                        }
                        lab5.updatePrediction(true);
                        movePcTo = getLabelAddr(instruction.getLabelName());
                    } else {
                        if (!pred) {
                            lab5.correctPreds++;
                        }

                        lab5.updatePrediction(false);
                    }
                    break; 
                case "lw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    memoryAddress = lab5.Registers[sourceIndex] + immediate;
                    lab5.Registers[targetIndex] = lab5.dataMemory[memoryAddress];

                    break; 
                case "sw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    int memoryAddressSW = lab5.Registers[sourceIndex] + immediate;
                    lab5.dataMemory[memoryAddressSW] = lab5.Registers[targetIndex];

                    break; 

                case "j":
                    movePcTo = getLabelAddr(instruction.getLabelName());                    

                    break;

                case "jr":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    movePcTo = lab5.Registers[sourceIndex];

                    break;

                case "jal":
                
                    lab5.Registers[31] = lab5.pc + 1;
                    movePcTo = getLabelAddr(instruction.getLabelName());
                

                    break;

                default:
                    System.out.println("invalid instruction: " + instruction.getOperationString() );
                    System.exit(0);
                    break;

            }

            return movePcTo;
        }

        public static int getLabelAddr(String labelName) {
            int addr = -1;
            
            if(labelName != null && labelName != "") {
                addr = lab5.labelToLineMap.get(labelName);
            }

            return addr;
        }
    }
