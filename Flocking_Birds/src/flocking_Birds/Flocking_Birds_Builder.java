package flocking_Birds;

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
	
	//Number of agents
	int environment_size = 100;
	int dull_birdCount = 50;
	int smart_birdCount = 100;
	int predator_birdCount = 3;
	int obstacle_count = 3;
	int food_count = 1;
	
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
		if(spawn_dull_birds){
			
			for(int i = 0; i < dull_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				context.add(new Dull_Bird(space, grid, angle));
			}
		}
		
		if(spawn_smart_birds){
			for(int i = 0; i < smart_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				context.add(new Smart_Bird(space, grid, angle));
			}
		}
		if(spawn_predator_birds){
			
			for(int i = 0; i < predator_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				context.add(new Predator_Bird(space, grid, angle));
			}
		}
		if(spawn_obstacles){
			
			for(int i = 0; i < obstacle_count; i++){
				context.add(new Obstacle(space, grid));
			}
		}
		if(spawn_food){
			
			for(int i = 0; i < food_count; i++){
				context.add(new Food(space, grid));
			}
		}
		//move agents
		for(Object obj : context){
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj,  (int)pt.getX(), (int)pt.getY());
		}
		return context;
	}

}
