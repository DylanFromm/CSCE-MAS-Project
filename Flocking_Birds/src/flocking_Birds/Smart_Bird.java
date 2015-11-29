package flocking_Birds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Smart_Bird {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean moved = false;
	private double angle = 0;
	private int distance = 5;
	
	public Smart_Bird(ContinuousSpace<Object> space, Grid<Object> grid, double angle){
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
		MooreQuery<Smart_Bird> smartquery = new MooreQuery(grid, this, 5, 5);
		Iterator<Smart_Bird> siter = smartquery.query().iterator();
		ArrayList<Smart_Bird> sbSet = new ArrayList<Smart_Bird>();
		while(siter.hasNext()){
			sbSet.add(siter.next());
		}
		SimUtilities.shuffle(sbSet, RandomHelper.getUniform());
		double Av = 0;
		double Cv = 0;

		move();
		if(!sbSet.isEmpty()){
			Av = Alignment(sbSet);
			Cv = Cohesion(sbSet);
			Separation(sbSet);
			this.angle = Math.atan2((Math.sin(Av) + /*Math.sin(this.angle) +*/ Math.sin(Cv))/2,
					(Math.cos(Av) + /*Math.cos(this.angle) +*/ Math.cos(Cv))/2);
			
		}
			
	
		moved = true;
		smart_collision();

	}
		
	//Standard move command, stays on the same course as before.
	public void move(){
		space.moveByVector(this, 1, this.angle,0);
		NdPoint myPoint = space.getLocation(this);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		moved = true;
	}

	public double magnitude(double x, double y){
		double mag = 0;
		
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		mag = Math.sqrt(x2 + y2);
		
		return mag;
	}
	public double Cohesion(List<Smart_Bird> smartBs){
		NdPoint thisLoc = space.getLocation(this);
		double avgdistance = 0;
		double AvgUnitVectX = Math.cos(this.angle);
		double AvgUnitVectY = Math.sin(this.angle);
		for(Smart_Bird sb : smartBs){
			NdPoint birdLoc = space.getLocation(sb);
			double xval = Math.abs(thisLoc.getX() - birdLoc.getX());
			double yval = Math.abs(thisLoc.getY() - birdLoc.getY());
			if(Math.hypot(xval, yval) < 6 ){
				avgdistance = avgdistance + Math.hypot(xval, yval)/2;
				AvgUnitVectX = AvgUnitVectX + (xval/magnitude(xval, yval));
				AvgUnitVectY = AvgUnitVectY + (yval/magnitude(xval, yval));
			}
		}
		avgdistance = avgdistance/smartBs.size();
		AvgUnitVectX = AvgUnitVectX/smartBs.size();
		AvgUnitVectY = AvgUnitVectY/smartBs.size();
		double avgdirection = Math.atan2(AvgUnitVectY, AvgUnitVectX);
		if(avgdistance > distance){
			NdPoint pt = space.getLocation(this);
			double moveX = pt.getX() + Math.cos(avgdirection)*.2;
			double moveY = pt.getY() + Math.sin(avgdirection)*.2;
			space.moveTo(this, moveX, moveY);
			return avgdirection;
		}else{
			return this.angle;
		}
	}
	public void Separation(List<Smart_Bird> smartBs){
		double angle_new = 0;
		int i = 0;
		NdPoint thisLoc = space.getLocation(this);
		NdPoint closestBirdLoc = null;
		double closestDistance = Double.MAX_VALUE;
		for(Smart_Bird sb : smartBs){
			NdPoint birdLoc = space.getLocation(sb);
	
			double xval = thisLoc.getX() - birdLoc.getX();
			double yval = thisLoc.getY() - birdLoc.getY();
			if(Math.hypot(xval, yval) < closestDistance){
				closestBirdLoc = birdLoc;
				closestDistance = Math.hypot(xval, yval);
			}
		}
		if(closestDistance < distance * .5){
			angle_new = (SpatialMath.calcAngleFor2DMovement(space, thisLoc, closestBirdLoc) - Math.PI)%(2*Math.PI);
			NdPoint pt = space.getLocation(this);
			double moveX = pt.getX() + Math.cos(angle_new)*.2;
			double moveY = pt.getY() + Math.sin(angle_new)*.2;
			space.moveTo(this, moveX, moveY);
		}

	}

	//move towards a different point.
	public double Alignment(List<Smart_Bird> smartBs){
		double AvgUnitVectX = Math.cos(this.angle);
		double AvgUnitVectY = Math.sin(this.angle);
		for(Smart_Bird sb : smartBs){
			double xval = Math.cos(sb.angle);
			double yval = Math.sin(sb.angle);
			AvgUnitVectX = AvgUnitVectX + (xval/magnitude(xval, yval));
			AvgUnitVectY = AvgUnitVectY + (yval/magnitude(xval, yval));
			
		}
		AvgUnitVectX = AvgUnitVectX/smartBs.size();
		AvgUnitVectY = AvgUnitVectY/smartBs.size();
		
		return Math.atan2(AvgUnitVectY, AvgUnitVectX);
	}
	public void smart_collision(){
		GridPoint pt = grid.getLocation(this);
		List<Object> smartBs = new ArrayList<Object>();
		for(Object obj : grid.getObjectsAt(pt.getX(), pt.getY())){
			if(obj instanceof Smart_Bird){
				smartBs.add(obj);
			}
		}
		if(smartBs.size() > 1){
				
			for(Object obj : smartBs){
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
			}

		}
	}

}