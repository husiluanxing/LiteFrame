package fys.core.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fys.core.config.AppConfig;
import fys.core.web.IController;

public class ControllerUtil {
	private static HashMap<Class, List<IController>> controllersPool = new HashMap<Class, List<IController>>();
	private static Lock lock = new ReentrantLock();  
	
	@SuppressWarnings("unchecked")
	public static IController getHandlerInstance(Class handlerClass, HttpServletRequest request, HttpServletResponse response) throws Exception {
		IController controller = findOneFromPool(handlerClass);
		if(controller != null){
			controller.setRequestResponse(request, response);
			return controller;
		}
		Class[] parameters = new Class[]{HttpServletRequest.class, HttpServletResponse.class};
		Constructor<IController> constructor = handlerClass.getConstructor(parameters);
		return constructor.newInstance(request, response);
	}
	
	public static void returnToPool(IController controller){
		controller.clear();
		if(controllersPool.containsKey(controller.getClass())){
			List<IController> list = controllersPool.get(controller.getClass());
			if(list.size() >= AppConfig.maxBufferControllerForOneMoudle){
				return;
			}
			lock.lock();
			try{
				list.add(controller);
			}
			finally{
				lock.unlock();
			}
		}
		else{
			lock.lock();
			try{
				List<IController> list = new LinkedList<IController>();
				list.add(controller);
				controllersPool.put(controller.getClass(), list);
			}
			finally{
				lock.unlock();
			}
		}
	}
	
	public static IController findOneFromPool(Class handlerClass){
		if(controllersPool.containsKey(handlerClass)){
			List<IController> list = controllersPool.get(handlerClass);
			lock.lock();
			try{
				if(list.size() > 0){
					return list.remove(0);
				}
			}
			finally{
				lock.unlock();
			}
		}
		return null;
	}
}
