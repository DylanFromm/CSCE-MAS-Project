package flocking_Birds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
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
	public double angle = 0;
	public int flock_flag = 0;
	private int distance = 5;
	public boolean predator_spotted = false;
	public boolean predator_clear = true;
	public boolean food_spotted = false;
	
	private int tick = 1;
	
	public Smart_Bird(ContinuousSpace<Object> space, Grid<Object> grid, double angle, int flock_flag){
		this.space =  space;
		this.grid = grid;
		this.angle = angle;
		this.flock_flag = flock_flag;
	}
	//When and how often this method will be called. will be called every time step
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		if(RepastEssentials.GetTickCount() % 20 == 0){
			this.flock_flag = RandomHelper.nextIntFromTo(1, 200);
		}
		//Get the grid location of this Bot
		GridPoint pt = grid.getLocation(this);
		//use the GridCellNgh class to create GridCells for the surrounding
		//neighborhood
		MooreQuery<Smart_Bird> smartquery = new MooreQuery(grid, this, 5, 5);
		Iterator<Smart_Bird> siter = smartquery.query().iterator();
		ArrayList<Smart_Bird> sbSet = new ArrayList<Smart_Bird>();
		while(siter.hasNext()){
			Object obj = siter.next();
			if(obj instanceof Smart_Bird){
				sbSet.add((Smart_Bird) obj);
			}
		}
		if(sbSet.size()>1){
			exchange_flag(sbSet);
		}
		SimUtilities.shuffle(sbSet, RandomHelper.getUniform());
		double Av = 0;
		double Cv = 0;
		if(Flocking_Birds_Builder.spawn_predator_birds){
			MooreQuery<Predator_Bird> predquery = new MooreQuery(grid, this, 5, 5);
			Iterator<Predator_Bird> piter = predquery.query().iterator();
			ArrayList<Predator_Bird> pdSet = new ArrayList<Predator_Bird>();
			while(piter.hasNext()){
				Object obj = piter.next();
				if(obj instanceof Predator_Bird){
					pdSet.add((Predator_Bird) obj);
				}
			}
			SimUtilities.shuffle(pdSet, RandomHelper.getUniform());
			if(!pdSet.isEmpty()){
				Predator_Spotted(sbSet, pdSet);
			}else if(pdSet.isEmpty() && predator_spotted){
				predator_clear = true;
				boolean clear = true;
				for(Smart_Bird sb : sbSet){
					clear &= sb.predator_spotted;
				}
				if(clear){
					predator_spotted = false;
				}
			}
			
			if(predator_spotted){
				Predator_Signal(sbSet);
				Separation(sbSet);
				move();
			}else{
				move();
				if(!sbSet.isEmpty()){
					Av = Alignment(sbSet);
					Cv = Cohesion(sbSet);
					Separation(sbSet);
					this.angle = Math.atan2((Math.sin(Av) +  Math.sin(Cv))/2,
								(Math.cos(Av) + Math.cos(Cv))/2);
				}
			}
			
		}
		if(Flocking_Birds_Builder.spawn_obstacles){
			MooreQuery<Obstacle> obsquery = new MooreQuery(grid, this, 6, 6);
			Iterator<Obstacle> oiter = obsquery.query().iterator();
			ArrayList<Obstacle> obsSet = new ArrayList<Obstacle>();
			while(oiter.hasNext()){
				Object obj = oiter.next();
				if(obj instanceof Obstacle){
					obsSet.add((Obstacle) obj);
				}
			}
			SimUtilities.shuffle(obsSet, RandomHelper.getUniform());
			NdPoint thisLoc = space.getLocation(this);
			if(!obsSet.isEmpty()){
				if(this.angle > Math.PI && this.angle <= 2*Math.PI){
					this.angle = -1*(this.angle - Math.PI);
				}
				
				for(Obstacle ob : obsSet){
					NdPoint obLoc = space.getLocation(ob);
					//get angle from the object to the bird.
					double angleto = SpatialMath.calcAngleFor2DMovement(space, thisLoc, obLoc);
					if(IsWithin(angleto+0.34906588779848602, angleto-0.34906588779848602, this.angle)){
						double dist1 = Math.hypot(thisLoc.getX() - Math.cos(angleto+0.34906588779848602), thisLoc.getY() - Math.sin(angleto+0.34906588779848602));
						double dist2 = Math.hypot(thisLoc.getX() - Math.cos(angleto-0.34906588779848602), thisLoc.getY() - Math.sin(angleto-0.34906588779848602));
						if(dist1 <= dist2){
							this.angle += 4*0.34906588779848602;
						}else{
							this.angle -= 4*0.34906588779848602;
						}
					}
					Av = Alignment(sbSet);
					Cv = Cohesion(sbSet);
					Separation(sbSet);
					this.angle = Math.atan2((Math.sin(Av) + Math.sin(this.angle) +  Math.sin(Cv))/3,
								(Math.cos(Av) + Math.cos(Cv) + Math.cos(this.angle))/3);
					move();
					
				}
			
				
				
				
			}else{
				move();
				if(!sbSet.isEmpty()){
					Av = Alignment(sbSet);
					Cv = Cohesion(sbSet);
					Separation(sbSet);
					this.angle = Math.atan2((Math.sin(Av) +  Math.sin(Cv))/2,
								(Math.cos(Av) + Math.cos(Cv))/2);
				}
			}
		}
		if(Flocking_Birds_Builder.spawn_food){
			MooreQuery<Food> foodquery = new MooreQuery(grid, this, 5, 5);
			Iterator<Food> fiter = foodquery.query().iterator();
			ArrayList<Food> fdSet = new ArrayList<Food>();
			while(fiter.hasNext()){
				Object obj = fiter.next();
				if(obj instanceof Food){
					fdSet.add((Food)obj);
				}
			}
			SimUtilities.shuffle(fdSet, RandomHelper.getUniform());
			if(!fdSet.isEmpty()){
				Food_Spotted(sbSet, fdSet);
			}
			move();
			if(!sbSet.isEmpty()){
				
				Av = Alignment(sbSet);
				Cv = Cohesion(sbSet);
				Separation(sbSet);
				this.angle = Math.atan2((Math.sin(Av) +  Math.sin(Cv))/2,
							(Math.cos(Av) + Math.cos(Cv))/2);
			}
		}
		

		
		if(!Flocking_Birds_Builder.spawn_predator_birds && !Flocking_Birds_Builder.spawn_food && !Flocking_Birds_Builder.spawn_obstacles){
			move();
			if(!sbSet.isEmpty()){
				Av = Alignment(sbSet);
				Cv = Cohesion(sbSet);
				Separation(sbSet);
				this.angle = Math.atan2((Math.sin(Av) +  Math.sin(Cv))/2,
							(Math.cos(Av) + Math.cos(Cv))/2);
			}	
		}
	
		moved = true;
		if(Flocking_Birds_Builder.collisions){
			smart_collision();
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
		
	//Standard move command, stays on the same course as before.
	public void move(){
		space.moveByVector(this, 1, this.angle,0);
		NdPoint myPoint = space.getLocation(this);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		moved = true;
	}
	public void Predator_Signal(List<Smart_Bird> smartBs){
		for(Smart_Bird sb : smartBs){
			sb.angle = Math.atan2((Math.sin(this.angle) + Math.sin(sb.angle))/2, (Math.cos(this.angle) + Math.cos(sb.angle)/2));
			sb.predator_spotted = true;
		}
	}
	public void Predator_Spotted(List<Smart_Bird> smartBs, List<Predator_Bird> predBs){
		predator_spotted = true;
		predator_clear = false;
		double predx = 0;
		double predy = 0;
		int i = 1;
		NdPoint thisLoc = space.getLocation(this);
		for(Predator_Bird pd : predBs){
			NdPoint predLoc = space.getLocation(pd);
			predx = predx + Math.cos((SpatialMath.calcAngleFor2DMovement(space, thisLoc, predLoc) - Math.PI)%(2*Math.PI));
			predy = predy + Math.sin((SpatialMath.calcAngleFor2DMovement(space, thisLoc, predLoc) - Math.PI)%(2*Math.PI));
			i++;
		}
		predx = predx/i;
		predy = predy/i;
		this.angle = Math.atan2((predy + Math.sin(this.angle))/2, (predx + Math.cos(this.angle)/2));
		for(Smart_Bird sb : smartBs){
			sb.angle = Math.atan2((predy + Math.sin(sb.angle))/2, (predx + Math.cos(sb.angle)/2));
			sb.predator_spotted = true;
		}
	}
	public void Food_Spotted(List<Smart_Bird> smartBs, List<Food> Food ){
		food_spotted = true;
		NdPoint thisLoc = space.getLocation(this);
		for(Food fd : Food){
			NdPoint fdLoc = space.getLocation(fd);
			this.angle = SpatialMath.calcAngleFor2DMovement(space, thisLoc, fdLoc);
		}
		for(Smart_Bird sb : smartBs){
			sb.angle = this.angle;
			sb.food_spotted = true;
		}
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
				Flocking_Birds_Builder.num_collisions++;
			}

		}
	}
	public void exchange_flag(List<Smart_Bird> smartBs){
		for(Smart_Bird sb : smartBs){
			sb.flock_flag = this.flock_flag;
		}
	}
}