/*
** list.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:09:49 2014 Maxime renaud
** Last update Thu Mar 13 23:47:38 2014 Maxime renaud
*/

# include	<stdlib.h>
# include	<string.h>
# include	"../../include/my_elf.h"

static	t_bool		add_symbol64(t_form **list, char type, char *name,
				   Elf64_Addr addr)
{
  t_form		*elm;

  if (!name)
    return (false);
  if (!(*list))
    {
      if (!((*list) = malloc(sizeof(t_form))))
	return (false);
      (*list)->addr64 = addr;
      (*list)->type = type;
      (*list)->name = name;
      (*list)->next = NULL;
      return (true);
    }
  if (!(elm = malloc(sizeof(t_form))))
    return (false);
  while ((*list)->next)
    list = &((*list)->next);
  (*list)->next = elm;
  elm->addr64 = addr;
  elm->type = type;
  elm->name = name;
  elm->next = NULL;
  return (true);
}

static	t_bool		add_symbol(t_form **list, char type, char *name,
				   Elf32_Addr addr)
{
  t_form		*elm;

  if (!name)
    return (false);
  if (!(*list))
    {
      if (!((*list) = malloc(sizeof(t_form))))
	return (false);
      (*list)->addr = addr;
      (*list)->type = type;
      (*list)->name = name;
      (*list)->next = NULL;
      return (true);
    }
  if (!(elm = malloc(sizeof(t_form))))
    return (false);
  while ((*list)->next)
    list = &((*list)->next);
  (*list)->next = elm;
  elm->addr = addr;
  elm->type = type;
  elm->name = name;
  elm->next = NULL;
  return (true);
}

extern	t_form		*create_list(t_elf *elf, t_sym *symbol)
{
  t_form		*list;
  Elf32_Sym		*edi;
  unsigned int		ecx;
  char			*name;

  ecx = 0;
  if (!(symbol->symbol))
    return (NULL);
  edi = symbol->symbol;
  edi--;
  list = NULL;
  while (ecx++ < symbol->ecx && ++edi)
    if ((name = symbol->string + edi->st_name) && name[0])
      if (add_symbol(&list,
		     getype(edi, elf, -1),
		     name,
		     edi->st_value) == false)
	return (NULL);
  return (list);
}

extern	t_form		*create_list64(t_elf *elf, t_sym_64 *symbol)
{
  t_form		*list;
  Elf64_Sym		*edi;
  unsigned int		ecx;
  char			*name;

  ecx = 0;
  if (!(symbol->symbol))
    return (NULL);
  edi = symbol->symbol;
  edi--;
  list = NULL;
  while (ecx++ < symbol->ecx && ++edi)
    if ((name = symbol->string + edi->st_name) && name[0])
      if (add_symbol64(&list,
		     getype64(edi, elf, -1),
		     name,
		     edi->st_value) == false)
	return (NULL);
  return (list);
}

extern	void		free_symbol(t_form *list)
{
  if (!list)
    return ;
  free_symbol(list->next);
  free(list);
  return ;
}
