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
		GridPoint pointWithFood = null;
		double maxCountFood = -1;
		
		//Get the grid location of this Bot
		GridPoint pt = grid.getLocation(this);
		
		//use the GridCellNgh class to create GridCells for the surrounding
		//neighborhood
		GridCellNgh<Dull_Bird> dull_nghCreator = new GridCellNgh<Dull_Bird>(grid, pt,
				Dull_Bird.class, 5, 5);
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
				Predator_Bird.class, 5, 5);
			List<GridCell<Predator_Bird>> gridCellsPredator = predator_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsPredator, RandomHelper.getUniform());
			
			
			for(GridCell<Predator_Bird> cell : gridCellsPredator){
				if(cell.size() > maxCountPred){
					pointWithMostPredBirds = cell.getPoint();
					maxCountPred = cell.size();
					
				}
			}
			
			
			
		}
		
		if(Flocking_Birds_Builder.spawn_obstacles){
			
			GridCellNgh<Obstacle> obstacle_nghCreator = new GridCellNgh<Obstacle>(grid, pt,
					Obstacle.class, 6, 6);
			List<GridCell<Obstacle>> gridCellsObstacle = obstacle_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsObstacle, RandomHelper.getUniform());
			
			
			
			for(GridCell<Obstacle> cell : gridCellsObstacle){
				if(cell.size() > maxCountObs){
					pointWithMostObstacles = cell.getPoint();
					maxCountObs = cell.size();
					
				}
			}
			
		}
		if(Flocking_Birds_Builder.spawn_food){
			GridCellNgh<Food> food_nghCreator = new GridCellNgh<Food>(grid, pt,
					Food.class, 5, 5);
			List<GridCell<Food>> gridCellsFood = food_nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCellsFood, RandomHelper.getUniform());
			
			
			for(GridCell<Food> cell : gridCellsFood){
				if(cell.size() > maxCountFood){
					pointWithFood = cell.getPoint();
					maxCountFood = cell.size();
					
				}
			}
		}
	
		
		if(maxCountDull > 0 || maxCountPred > 0 || maxCountObs > 0 || maxCountFood > 0){
			moveTowards(pointWithMostBirds, pointWithMostObstacles, pointWithMostPredBirds, pointWithFood, maxCountDull, maxCountObs, maxCountPred,maxCountFood);
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
	
	
	public void moveTowards(GridPoint ptdull, GridPoint ptobs, GridPoint ptpred, GridPoint ptfood, double maxCountDull, double maxCountObs, double maxCountPred, double maxCountFood){
		//only move if we are not already in this grid location
		//if(!pt.equals(grid.getLocation(this))){
			double i = 2.0;
			//Get birds location as a point, NdPoint stores its coordinates as doubles.
			NdPoint myPoint = space.getLocation(this);
			
			//Get new point's location
			NdPoint otherPointDull = new NdPoint(ptdull.getX(), ptdull.getY());
			boolean obstacle = false;
			//Calculate the angle for the bird to move to get to new point			double angle_new = this.angle;
			double xunit_new = Math.cos(this.angle)*2;
			double yunit_new = Math.sin(this.angle)*2;
			if(maxCountDull > 0){
				xunit_new =xunit_new + Math.cos(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointDull) - Math.PI);
				yunit_new =yunit_new + Math.sin(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointDull) - Math.PI);
				i++;
			}
			if(Flocking_Birds_Builder.spawn_obstacles && maxCountObs > 0){
				NdPoint otherPointObs = new NdPoint(ptobs.getX(), ptobs.getY());
				double angleto = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointObs);
				if(IsWithin(angleto+0.34906588779848602, angleto-0.34906588779848602, this.angle)){
					double dist1 = Math.hypot(myPoint.getX() - Math.cos(angleto+0.34906588779848602), myPoint.getY() - Math.sin(angleto+0.34906588779848602));
					double dist2 = Math.hypot(myPoint.getX() - Math.cos(angleto-0.34906588779848602), myPoint.getY() - Math.sin(angleto-0.34906588779848602));
					obstacle = true;
					if(dist1 <= dist2){
						this.angle += .34906588779848602;
					}else{
						this.angle -= .34906588779848602;
					}
				}
				i++;
			}
			
			if(Flocking_Birds_Builder.spawn_predator_birds && maxCountPred > 0){
				NdPoint otherPointPred = new NdPoint(ptpred.getX(), ptpred.getY());
				xunit_new = xunit_new + Math.cos(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointPred) - Math.PI);
				yunit_new = yunit_new + Math.sin(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointPred) - Math.PI);
				i++;
			}
			
			if(Flocking_Birds_Builder.spawn_food && maxCountFood > 0){
				NdPoint otherPointFood = new NdPoint(ptfood.getX(), ptfood.getY());
				xunit_new = xunit_new + Math.cos(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointFood));
				yunit_new = yunit_new + Math.sin(SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPointFood));
				i++;
			}
			if(!obstacle){
				this.angle = Math.atan2(yunit_new/i, xunit_new/i);
			}
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
	public boolean IsWithin(double angle1, double angle2, double angleinquest){
		if(angle1 <= angle2){
			if(angleinquest <= angle2 && angleinquest >= angle1){
				return true;
			}else{
				return false;
			}
		}else{
			if(angleinquest <= angle1 && angleinquest >= angle2){
				return true;
			}else{
				return false;
			}
		}
	}
}
