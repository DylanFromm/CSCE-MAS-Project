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
		GridPoint pointWithMostPredBirds = null;
		double maxCountPred = -1;
		GridPoint pointWithMostObstacles = null;
		double maxCountObs = -1;
		
		//Get the grid location of this Bot
		GridPoint pt = grid.getLocation(this);
		
		//use the GridCellNgh class to create GridCells for the surrounding
		//neighborhood
		GridCellNgh<Dull_Bird> dull_nghCreator = new GridCellNgh<Dull_Bird>(grid, pt,
				Dull_Bird.class, 2, 2);
		List<GridCell<Dull_Bird>> gridCellsDull = dull_nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCellsDull, RandomHelper.getUniform());
		GridPoint pointWithMostBirds = null;
		double maxCountDull = -1;
		for(GridCell<Dull_Bird> cell : gridCellsDull){
			if(cell.size() > maxCountDull){
				pointWithMostBirds = cell.getPoint();
				if(!pointWithMostBirds.equals(grid.getLocation(this))){
					maxCountDull = cell.size();
				}
			}
		}
		
		if(Flocking_Birds_Builder.spawn_predator_birds){
			
			GridCellNgh<Predator_Bird> predator_nghCreator = new GridCellNgh<Predator_Bird>(grid, pt,
				Predator_Bird.class, 2, 2);
			List<GridCell<Predator_Bird>> gridCellsPredator = predator_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsPredator, RandomHelper.getUniform());
			
			
			for(GridCell<Predator_Bird> cell : gridCellsPredator){
				if(cell.size() > maxCountPred){
					pointWithMostPredBirds = cell.getPoint();
					if(!pointWithMostPredBirds.equals(grid.getLocation(this))){
						maxCountPred = cell.size();
					}
				}
			}
			
			
			
		}
		
		if(Flocking_Birds_Builder.spawn_obstacles){
			
			GridCellNgh<Obstacle> obstacle_nghCreator = new GridCellNgh<Obstacle>(grid, pt,
					Obstacle.class, 3, 3);
			List<GridCell<Obstacle>> gridCellsObstacle = obstacle_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsObstacle, RandomHelper.getUniform());
			
			
			
			for(GridCell<Obstacle> cell : gridCellsObstacle){
				if(cell.size() > maxCountObs){
					pointWithMostObstacles = cell.getPoint();
					if(!pointWithMostObstacles.equals(grid.getLocation(this))){
						maxCountObs = cell.size();
					}
				}
			}
			
		}
	
		
		if(maxCountDull > 0 || maxCountPred > 0 || maxCountObs > 0){
			moveTowards(pointWithMostBirds, pointWithMostObstacles, pointWithMostPredBirds);
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
	
	
	public void moveTowards(GridPoint ptdull, GridPoint ptobs, GridPoint ptpred){
		//only move if we are not already in this grid location
		//if(!pt.equals(grid.getLocation(this))){
			double i = 1.0;
			//Get birds location as a point, NdPoint stores its coordinates as doubles.
			NdPoint myPoint = space.getLocation(this);
			
			//Get new point's location
			NdPoint otherPointDull = new NdPoint(ptdull.getX(), ptdull.getY());
			
			//Calculate the angle for the bird to move to get to new point
			double angle_new = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointDull) - Math.PI;

			if(Flocking_Birds_Builder.spawn_obstacles && ptobs != null){
				NdPoint otherPointObs = new NdPoint(ptobs.getX(), ptobs.getY());
				angle_new = (angle_new + (SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointObs) - Math.PI));
				i++;
			}
			
			if(Flocking_Birds_Builder.spawn_predator_birds && ptpred != null){
				NdPoint otherPointPred = new NdPoint(ptpred.getX(), ptpred.getY());
				angle_new = (angle_new + (SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointPred) - Math.PI));
				i++;
			}
			angle_new = angle_new/i;
			
			this.angle = (6*this.angle + angle_new)/7.0;
			//Moves bird along calculated angle, by 1 space
			space.moveByVector(this, 1, this.angle,0);
			
			//Updates the birds position in the grid by converting its locaiton in
			//continuous space to int coordinates appropriate for a grid.
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			
			moved = true;
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
