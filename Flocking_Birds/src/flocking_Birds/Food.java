package flocking_Birds;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Food {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	public int Landed_count = 0;
	public Food(ContinuousSpace<Object> space, Grid<Object> grid){
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void Check_Collisions(){
		GridPoint pt = grid.getLocation(this);
		
		GridCellNgh<Dull_Bird> dull_nghCreator = new GridCellNgh<Dull_Bird>(grid, pt,
				Dull_Bird.class, 1, 1 );
		GridCellNgh<Smart_Bird> smart_nghCreator = new GridCellNgh<Smart_Bird>(grid, pt,
				Smart_Bird.class, 1, 1);
		
		List<GridCell<Dull_Bird>> gridCellsD = dull_nghCreator.getNeighborhood(true);
		List<GridCell<Smart_Bird>> gridCellsS = smart_nghCreator.getNeighborhood(true);
		for(GridCell<Dull_Bird> cell : gridCellsD){
			if(cell.size() > 0){
				GridPoint pt2 = cell.getPoint();
				Object obj = grid.getObjectAt(pt2.getX(), pt2.getY());
				if(obj != this){
					Context<Object> context = ContextUtils.getContext(obj);
					context.remove(obj);
					this.Landed_count++;
				}
			}
		}
		for(GridCell<Smart_Bird> cell : gridCellsS){
			if(cell.size() > 0){
				GridPoint pt2 = cell.getPoint();
				Object obj = grid.getObjectAt(pt2.getX(), pt2.getY());
				if(obj != this){
					Context<Object> context = ContextUtils.getContext(obj);
					context.remove(obj);
					this.Landed_count++;
				}
			}
		}
	}
}
