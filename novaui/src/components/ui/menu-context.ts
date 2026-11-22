import { createContext, useContext } from 'react';

export type MenuContextValue = { close: () => void };

export const MenuContext = createContext<MenuContextValue>({ close: () => undefined });

export const useMenuContext = (): MenuContextValue => useContext(MenuContext);
