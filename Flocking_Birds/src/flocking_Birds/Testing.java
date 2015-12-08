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
	private ArrayList<Dull_Bird> dbSet = new ArrayList<Dull_Bird>();
	private ArrayList<Predator_Bird> pbSet = new ArrayList<Predator_Bird>();
	private ArrayList<Obstacle> obSet = new ArrayList<Obstacle>();
	private ArrayList<Food> fdSet = new ArrayList<Food>();
	
	public Testing(ContinuousSpace<Object> space, Grid<Object> grid, 
			ArrayList<Smart_Bird> sbSet, ArrayList<Dull_Bird> dbSet,
			ArrayList<Predator_Bird> pbSet, ArrayList<Obstacle> obSet,ArrayList<Food> fdSet ){
		this.space =  space;
		this.grid = grid;
		this.sbSet = sbSet;
		this.dbSet = dbSet;
		this.pbSet = pbSet;
		this.obSet = obSet;
		this.fdSet = fdSet;
	}
	@ScheduledMethod(start = 1, interval = 1)
	public void testing(){

		//more birds per area the faster the flocks will form.
		if(Flocking_Birds_Builder.hypothesis_I){
			if(Flocking_Birds_Builder.spawn_smart_birds){
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
		}
		if(Flocking_Birds_Builder.hypothesis_II){
			
		}
		if(Flocking_Birds_Builder.hypothesis_III){
			
		}
		if(Flocking_Birds_Builder.hypothesis_IV){
			if(Flocking_Birds_Builder.spawn_dull_birds){
				int landed_tot = 0;
				for(Food fd : this.fdSet){
					landed_tot += fd.Landed_count;
				}
				if(landed_tot == Flocking_Birds_Builder.dull_birdCount){
					RunEnvironment.getInstance().endRun();
				}
			}
			if(Flocking_Birds_Builder.spawn_smart_birds){
				int landed_tot = 0;
				for(Food fd : this.fdSet){
					landed_tot += fd.Landed_count;
				}
				if(landed_tot == Flocking_Birds_Builder.smart_birdCount){
					RunEnvironment.getInstance().endRun();
				}
			}
		}
	}
	
}
