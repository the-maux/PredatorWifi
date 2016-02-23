/*
** load.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:10:23 2014 Maxime renaud
** Last update Sun Mar 16 20:20:28 2014 Maxime renaud
*/

# include	<fcntl.h>
# include	<stdio.h>
# include	<stdlib.h>
# include	<unistd.h>
# include	<string.h>
# include	"../../include/my_elf.h"

static	t_bool	get_symb(t_elf *elf, Elf32_Shdr *symh, Elf32_Shdr *strh,
			 t_bool	flag)
{
  t_sym		*ptr;

  if (strh == (Elf32_Shdr *)(0x01) || !strh || !symh)
    {
      printf("nm: no symbols\n");
      return (false);
    }
  if (flag == true)
    ptr = &(elf->sym_dyna);
  else
    ptr = &(elf->sym_stat);
  ptr->symbol = (Elf32_Sym*)(elf->maddr + symh->sh_offset);
  ptr->string = (elf->maddr + strh->sh_offset);
  ptr->ecx = symh->sh_size / sizeof(Elf32_Sym);
  if (ptr->ecx > 500)
    {
      printf("nm: no symbols\n");
      return (false);
    }
  return (true);
}

static	void	init_for_norme(void **p1, void **p2,
			       void **p3, void **p4)
{
  *p1 = NULL;
  *p2 = NULL;
  *p3 = NULL;
  *p4 = NULL;
  return ;
}

extern	t_bool	get_symbol_tab_head(t_elf *elf, int ecx, Elf32_Shdr *edi)
{
  Elf32_Shdr	*symh;
  Elf32_Shdr	*strh;
  Elf32_Shdr	*dynsymh;
  Elf32_Shdr	*dynstrh;

  init_for_norme((void**)&symh, (void**)&strh,
		 (void**)&dynsymh, (void**)&dynstrh);
  while (++ecx < elf->ehdr->e_shnum)
    {
      if (edi->sh_type == SHT_SYMTAB)
	symh = edi;
      else if (edi->sh_type == SHT_DYNSYM)
	dynsymh = edi;
      else if (edi->sh_type == SHT_STRTAB &&
	       !strcmp(".strtab", elf->shstrtab + edi->sh_name))
	strh = edi;
      else if (edi->sh_type == SHT_STRTAB &&
	       !strcmp(".dynstr", elf->shstrtab + edi->sh_name))
	dynstrh = edi;
      edi++;
    }
  if (elf->ehdr->e_type == ET_REL)
    return (get_symb(elf, symh, strh, true));
  return ((get_symb(elf, dynsymh, dynstrh, false) == true) &&
	  ((get_symb(elf, symh, strh, true) == true)) ? (true) : (false));
}

static	t_bool	get_symb64(t_elf *elf, Elf64_Shdr *symh, Elf64_Shdr *strh,
			 t_bool	flag)
{
  t_sym_64	*ptr;

  if (strh == (Elf64_Shdr*)(0x1) || strh == 0x00 || symh == 0x00)
    {
      printf("nm: no symbols\n");
      return (false);
    }
  if (flag == true)
    ptr = &(elf->sym_dyna64);
  else
    ptr = &(elf->sym_stat64);
  ptr->symbol = (elf->maddr + symh->sh_offset);
  ptr->string = elf->maddr + strh->sh_offset;
  ptr->ecx = symh->sh_size / sizeof(Elf64_Sym);
  return (true);
}

extern	t_bool	get_symbol_tab_head64(t_elf *elf, int ecx, Elf64_Shdr *edi)
{
  Elf64_Shdr	*symh;
  Elf64_Shdr	*strh;
  Elf64_Shdr	*dynsymh;
  Elf64_Shdr	*dynstrh;

  init_for_norme((void**)&symh, (void**)&strh,
		 (void**)&dynsymh, (void**)&dynstrh);
  while (++ecx < elf->ehdr_64->e_shnum)
    {
      if (edi->sh_type == SHT_SYMTAB)
	symh = edi;
      else if (edi->sh_type == SHT_DYNSYM)
	dynsymh = edi;
      else if (edi->sh_type == SHT_STRTAB &&
	       !strcmp(".strtab", elf->shstrtab + edi->sh_name))
	strh = edi;
      else if (edi->sh_type == SHT_STRTAB &&
	       !strcmp(".dynstr", elf->shstrtab + edi->sh_name))
	dynstrh = edi;
      edi++;
    }
  if (elf->ehdr_64->e_type == ET_REL)
    return (get_symb64(elf, symh, strh, true));
  return ((get_symb64(elf, dynsymh, dynstrh, false) == true) &&
	  ((get_symb64(elf, symh, strh, true) == true)) ? (true) : (false));
}
