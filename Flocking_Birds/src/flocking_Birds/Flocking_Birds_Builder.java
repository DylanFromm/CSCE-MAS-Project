package flocking_Birds;

import java.util.ArrayList;


import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;


public class Flocking_Birds_Builder implements ContextBuilder<Object> {
	//If bool = true, object will be spawned. if bool = false object will not be spawned.
	public static boolean spawn_dull_birds = false;
	public static boolean spawn_smart_birds = true;
	public static boolean spawn_predator_birds = false;
	public static boolean spawn_obstacles = true;
	public static boolean spawn_food = false;
	public static boolean collisions = false;
	
	public static boolean hypothesis_I = false;
	public static boolean hypothesis_II = true;
	public static boolean hypothesis_III = false;
	public static boolean hypothesis_IV = false;
	
	//Number of agents
	public static int environment_size = 100;
	public static int dull_birdCount = 150;
	public static int smart_birdCount = 150;
	public static int predator_birdCount = 3;
	public static int obstacle_count = 1;
	public static int food_count = 1;
	public static int num_collisions = 0;
	
	//Context is a named set of agents.
	@Override
	public Context build(Context<Object> context) {
		context.setId("Flocking_Birds");
		/*
		 * To make both continuous space and grid variables, we use factories for setup.
		 * Each take the following parameters
		 * 
		 * -Name of grid or space
		 * -Context to associate the grid or space with
		 * -Adder which determines where objects added to the grid or space will be
		 * initially located
		 * -Class that describes how the borders are setup (with or without edges)
		 * -Dimensions of the grid (100 x 100)
		 */
		ContinuousSpaceFactory spaceFactory = 
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space =
				spaceFactory.createContinuousSpace("space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(), 100,100);
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(),
						true,environment_size, environment_size));
		
		//populate the space with agents
		ArrayList<Dull_Bird> dbSet = new ArrayList<Dull_Bird>();
		if(spawn_dull_birds){
			
			for(int i = 0; i < dull_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				Dull_Bird db = new Dull_Bird(space, grid, angle);
				dbSet.add(db);
				context.add(db);
			}
		}
		ArrayList<Smart_Bird> sbSet = new ArrayList<Smart_Bird>();
		if(spawn_smart_birds){
			for(int i = 0; i < smart_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				int flock_flag = RandomHelper.nextIntFromTo(1,200);
				Smart_Bird sb = new Smart_Bird(space, grid, angle,flock_flag);
				sbSet.add(sb);
				context.add(sb);
				
			}
		}
		ArrayList<Predator_Bird> pbSet = new ArrayList<Predator_Bird>();
		if(spawn_predator_birds){
			
			for(int i = 0; i < predator_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				Predator_Bird pb = new Predator_Bird(space, grid, angle);
				pbSet.add(pb);
				context.add(pb);
			}
		}
		ArrayList<Obstacle> obSet = new ArrayList<Obstacle>();
		
		if(spawn_obstacles){
			
			for(int i = 0; i < obstacle_count; i++){
				Obstacle ob = new Obstacle(space, grid);
				obSet.add(ob);
				context.add(ob);
			}
		}
		
		ArrayList<Food> fdSet = new ArrayList<Food>();
		if(spawn_food){
			
			for(int i = 0; i < food_count; i++){
				Food fd = new Food(space,grid);
				fdSet.add(fd);
				context.add(fd);
			}
		}
		context.add(new Testing(space, grid, sbSet, dbSet, pbSet, obSet, fdSet));
		//move agents
		for(Object obj : context){
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj,  (int)pt.getX(), (int)pt.getY());
		}
		return context;
	}
	public int getCollisions(){
		return this.num_collisions;
	}
	

}
