package merger;

import java.util.LinkedList;
import java.util.Observable;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import main.Blame;

public class MergeVisitor 
//#conflictsAnalyzer
extends Observable
//end of #conflictsAnalyzer
{
	//normalization study
	private boolean isMergeCommit;
	//end of normalization study

	private LinkedList<MergerInterface> mergerList = new LinkedList<MergerInterface>();

	public void registerMerger(MergerInterface merger) {
		mergerList.add(merger);
	}

	private LinkedList<MergerInterface> getMergerList() {
		return mergerList;
	}

	public void visit(FSTNode current) {
		if(current instanceof FSTNonTerminal) {
			for(FSTNode child : ((FSTNonTerminal)current).getChildren())
				visit(child);
		} else if(current instanceof FSTTerminal) {
			for(MergerInterface merger : getMergerList()) {
				try {
					//normalization study
					if(this.nodeWasChanged((FSTTerminal) current)){
						setChanged();
						notifyObservers(current);
					}
					//end of normalization study
					if(((FSTTerminal)current).getBody().contains(FSTGenMerger.MERGE_SEPARATOR)) {

						merger.merge((FSTTerminal)current);

						//#conflictsAnalyzer
						//perform conflict pattern analysis and removal of spacing conflicts

						/*String nodeBody = ((FSTTerminal)current).getBody();
						if(nodeBody.contains(FSTGenMerger.MERGE_SEPARATOR) || nodeBody.contains(FSTGenMerger.DIFF3MERGE_SEPARATOR)) {
							setChanged();
							notifyObservers(current);
						}else if(nodeBody.contains(Blame.LEFT_SEPARATOR) || nodeBody.contains(Blame.RIGHT_SEPARATOR)){
							setChanged();
							notifyObservers(current);
						}*/

						//end of #conflictsAnalyzer


					}

				} catch (ContentMergeException e) {
					System.err.println(e.toString());
				} 
			}

		} else {
			System.err.println("MergerVisitor: node is neither non-terminal nor terminal!");			
		}
	}

	//normalization study
	public void setIsMergeCommit (Boolean isMC){
		this.isMergeCommit = isMC;
	}

	public boolean nodeWasChanged(FSTTerminal node){
		boolean nodeWasChanged = false;
			if(node.getBody().contains(FSTGenMerger.MERGE_SEPARATOR)){
				String body = node.getBody() + " ";
				String[] tokens = body.split(FSTGenMerger.MERGE_SEPARATOR);
				tokens[0] = tokens[0].replace(FSTGenMerger.SEMANTIC_MERGE_MARKER, "").trim();
				tokens[1] = tokens[1].trim();

				if(this.isMergeCommit){
					tokens[2] = tokens[2].trim();
				}

				if(!tokens[0].equals(tokens[1])){
					nodeWasChanged = true;
				}else if(this.isMergeCommit){
					if(!tokens[2].equals(tokens[1])){
						nodeWasChanged = true;
					}
				}
			}else{
				nodeWasChanged = true;
			}	
		return nodeWasChanged;
	}
	
	//normalization study
}
