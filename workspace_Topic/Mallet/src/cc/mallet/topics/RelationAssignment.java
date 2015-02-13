package cc.mallet.topics;

import java.io.Serializable;
import cc.mallet.types.*;

/** This class combines a sequence of observed features
 *   with a sequence of hidden "labels".
 */

public class RelationAssignment implements Serializable {
	public Instance_new instance;
	public LabelSequence relationSequence;
	public Labeling relationDistribution;
                
	public RelationAssignment (Instance_new instance, LabelSequence relationSequence) {
		this.instance = instance;
		this.relationSequence = relationSequence;
	}
}