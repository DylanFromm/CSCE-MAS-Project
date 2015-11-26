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
	public static boolean spawn_dull_birds = true;
	public static boolean spawn_smart_birds = false;
	public static boolean spawn_predator_birds = false;
	public static boolean spawn_obstacles = false;
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
						true, 100, 100));
		
		//populate the space with dull birds
		if(spawn_dull_birds){
			int dull_birdCount = 50;
			for(int i = 0; i < dull_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				context.add(new Dull_Bird(space, grid, angle));
			}
		}
		
		if(spawn_smart_birds){
			
		}
		if(spawn_predator_birds){
			int predator_birdCount = 2;
			for(int i = 0; i < predator_birdCount; i++){
				double angle = RandomHelper.nextDoubleFromTo(0, 2*Math.PI);
				context.add(new Predator_Bird(space, grid, angle));
			}
		}
		if(spawn_obstacles){
			int obstacle_count = 5;
			for(int i = 0; i < obstacle_count; i++){
				context.add(new Obstacle(space, grid));
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
