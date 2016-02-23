/*
** sort.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:10:08 2014 Maxime renaud
** Last update Sun Mar 16 20:58:26 2014 Maxime renaud
*/

# include	<string.h>
# include	"../../include/my_elf.h"

static	void	replace(t_form *max, t_form *min)
{
  t_form	swap;

  swap.type = min->type;
  swap.name = min->name;
  swap.addr = min->addr;
  min->type = max->type;
  min->name = max->name;
  min->addr = max->addr;
  max->type = swap.type;
  max->name = swap.name;
  max->addr = swap.addr;
  return ;
}

static	void	replace64(t_form *max, t_form *min)
{
  t_form	swap;

  swap.type = min->type;
  swap.name = min->name;
  swap.addr64 = min->addr64;
  min->type = max->type;
  min->name = max->name;
  min->addr64 = max->addr64;
  max->type = swap.type;
  max->name = swap.name;
  max->addr64 = swap.addr64;
  return ;
}

static	void	replace_first(t_form **list, t_bool flag, t_form **first)
{
  t_form	*tmp;

  if ((*list) && (*list)->name)
    tmp = *list;
  while (*list && (*list)->next)
    {
      if (strcmp(tmp->name, (*list)->name) > 0)
	tmp = *list;
      list = &((*list)->next);
    }
  if (flag == true)
    replace(tmp, *first);
  else
    replace64(tmp, *first);
  return ;
}

extern	void	sort(t_form **list, t_form *first, t_bool flag)
{
  t_form	*tmp;

  replace_first(list, flag, list);
  first = *list;
  while ((*list) && (*list)->next)
    {
      if (strcmp((*list)->name, (*list)->next->name) > 0)
	{
	  if (flag == true)
	    replace((*list), (*list)->next);
	  else
	    replace64((*list), (*list)->next);
	  list = &(first);
	}
      list = &((*list)->next);
    }
  return ;
}
