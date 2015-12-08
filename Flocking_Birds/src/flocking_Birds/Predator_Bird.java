package flocking_Birds;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Predator_Bird {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean moved = false;
	private double angle;
	public int eaten = 0;
	
	public Predator_Bird(ContinuousSpace<Object> space, Grid<Object> grid, double angle){
		this.space =  space;
		this.grid = grid;
		this.angle = angle;
	}
	//When and how often this method will be called. will be called every time step
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		//Get the grid location of this Bot
		GridPoint pt = grid.getLocation(this);
		
		if(Flocking_Birds_Builder.spawn_dull_birds && !Flocking_Birds_Builder.spawn_smart_birds){
			//use the GridCellNgh class to create GridCells for the surrounding
			//neighborhood
			GridCellNgh<Dull_Bird> dull_nghCreator = new GridCellNgh<Dull_Bird>(grid, pt,
					Dull_Bird.class, 5, 5);
		
			//Import repast.simphony.query.space.grid.GridCell
			List<GridCell<Dull_Bird>> gridCells = dull_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
			GridPoint pointWithMostBirds = null;
			int maxCount = 0;
			for(GridCell<Dull_Bird> cell : gridCells){
				if(cell.size() > maxCount){
					pointWithMostBirds = cell.getPoint();
					maxCount = cell.size();
				}
			}
			if(maxCount == 0){
				move();
			}else{
				moveTowards(pointWithMostBirds);
			}
		}else if(!Flocking_Birds_Builder.spawn_dull_birds && Flocking_Birds_Builder.spawn_smart_birds){
			//use the GridCellNgh class to create GridCells for the surrounding
			//neighborhood
			GridCellNgh<Smart_Bird> smart_nghCreator = new GridCellNgh<Smart_Bird>(grid, pt,
					Smart_Bird.class, 5, 5);
		
			//Import repast.simphony.query.space.grid.GridCell
			List<GridCell<Smart_Bird>> gridCells = smart_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
			GridPoint pointWithMostBirds = null;
			int maxCount = 0;
			for(GridCell<Smart_Bird> cell : gridCells){
				if(cell.size() > maxCount){
					pointWithMostBirds = cell.getPoint();
					maxCount = cell.size();
				}
			}
			if(maxCount == 0){
				move();
			}else{
				moveTowards(pointWithMostBirds);
			}
		}
		collision();
	}
	public void move(){
		space.moveByVector(this, 1, this.angle,0);
		NdPoint myPoint = space.getLocation(this);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		moved = true;
	}
	public void moveTowards(GridPoint pt){
		//only move if we are not already in this grid location
		if(!pt.equals(grid.getLocation(this))){
			//Get birds location as a point, NdPoint stores its coordinates as doubles.
			NdPoint myPoint = space.getLocation(this);
					
			//Get new point's location
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
		
			//Calculate the angle for the bird to move to get to new point
			double xunit = Math.cos(this.angle);
			double yunit = Math.sin(this.angle);
			xunit += Math.cos(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint));
			yunit += Math.sin(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint));
			this.angle = Math.atan2(yunit/2, xunit/2);
			//Moves bird along calculated angle, by 1 space
			space.moveByVector(this, 1, this.angle,0);
					
			//Updates the birds position in the grid by converting its locaiton in
			//continuous space to int coordinates appropriate for a grid.
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
					
			moved = true;
		}
	}
	public void collision(){
		if(Flocking_Birds_Builder.spawn_dull_birds && !Flocking_Birds_Builder.spawn_smart_birds){
			GridPoint pt = grid.getLocation(this);
			List<Object> dullBs = new ArrayList<Object>();
			for(Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
				if(obj instanceof Dull_Bird){
					dullBs.add(obj);
				}
			}
			if(dullBs.size() >= 1){
				
				for(Object obj : dullBs){
					
					Context<Object> context = ContextUtils.getContext(obj);
					context.remove(obj);
					this.eaten++;
				}

			}	
			
		}else if(!Flocking_Birds_Builder.spawn_dull_birds && Flocking_Birds_Builder.spawn_smart_birds){
			GridPoint pt = grid.getLocation(this);
			List<Object> smartBs = new ArrayList<Object>();
			for(Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
				if(obj instanceof Smart_Bird){
					smartBs.add(obj);
				}
			}
			if(smartBs.size() >= 1){
				
				for(Object obj : smartBs){
					
					Context<Object> context = ContextUtils.getContext(obj);
					context.remove(obj);
					this.eaten++;
				}

			}
		}
	}
}
