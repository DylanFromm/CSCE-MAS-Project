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

public class Dull_Bird {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean moved = false;
	private double angle;
	
	public Dull_Bird(ContinuousSpace<Object> space, Grid<Object> grid, double angle){
		this.space =  space;
		this.grid = grid;
		this.angle = angle;
	}
	//When and how often this method will be called. will be called every time step
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		//Get the grid location of this Bot
		GridPoint pt = grid.getLocation(this);
		
		//use the GridCellNgh class to create GridCells for the surrounding
		//neighborhood
		GridCellNgh<Dull_Bird> dull_nghCreator = new GridCellNgh<Dull_Bird>(grid, pt,
				Dull_Bird.class, 2, 2);
		
		//Import repast.simphony.query.space.grid.GridCell
		List<GridCell<Dull_Bird>> gridCells = dull_nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		GridPoint pointWithMostBirds = null;
		double maxCount = -1;
		for(GridCell<Dull_Bird> cell : gridCells){
			if(cell.size() > maxCount){
				pointWithMostBirds = cell.getPoint();
				if(!pointWithMostBirds.equals(grid.getLocation(this))){
					maxCount = cell.size();
				}
			}
		}
		if(maxCount > 0){
			moveTowards(pointWithMostBirds);
			//move();
		}else{
			move();
		}
		dull_collision();
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
		//if(!pt.equals(grid.getLocation(this))){
			//Get birds location as a point, NdPoint stores its coordinates as doubles.
			NdPoint myPoint = space.getLocation(this);
			
			//Get new point's location
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			
			//Calculate the angle for the bird to move to get to new point
			double angle_new = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			//angle_new = Math.toRadians(Math.toDegrees(angle_new)+180);
			this.angle = (3*this.angle + (angle_new - Math.PI))/4.0;
			//Moves bird along calculated angle, by 1 space
			space.moveByVector(this, 1, this.angle,0);
			
			//Updates the birds position in the grid by converting its locaiton in
			//continuous space to int coordinates appropriate for a grid.
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			
			moved = true;
		//}
	}
	public void dull_collision(){
		GridPoint pt = grid.getLocation(this);
		List<Object> dullBs = new ArrayList<Object>();
		for(Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if(obj instanceof Dull_Bird){
				dullBs.add(obj);
			}
		}
		if(dullBs.size() > 1){
			
			for(Object obj : dullBs){
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
			}

		}
	}
}
