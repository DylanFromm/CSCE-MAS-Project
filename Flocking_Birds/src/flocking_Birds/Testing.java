package flocking_Birds;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Testing {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private ArrayList<Smart_Bird> sbSet = new ArrayList<Smart_Bird>();
	public Testing(ContinuousSpace<Object> space, Grid<Object> grid, ArrayList<Smart_Bird> sbSet){
		this.space =  space;
		this.grid = grid;
		this.sbSet = sbSet;
	}
	@ScheduledMethod(start = 1, interval = 1)
	public void testing(){

		//more birds per area the faster the flocks will form.
		if(Flocking_Birds_Builder.hypothesis_I){

			boolean flocked = true;
			boolean first = true;
			int flag = 0;
			for(Smart_Bird sb : this.sbSet){
				
				if(first){
					flag = sb.flock_flag;
					first = false;
				}else{
					if(sb.flock_flag != flag){
						flocked = false;
					}
				}
			}
			if(flocked){
				RunEnvironment.getInstance().endRun();
			}
		}
		if(Flocking_Birds_Builder.hypothesis_II){
			
		}
		if(Flocking_Birds_Builder.hypothesis_III){
			
		}
		if(Flocking_Birds_Builder.hypothesis_IV){
			
		}
	}
	
}
